package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.CreateNewSession;
import no.java.sleepingpill.core.commands.HasDataInput;
import no.java.sleepingpill.core.commands.NewSpeaker;
import no.java.sleepingpill.core.commands.UpdateSession;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonNull;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionService {

    private static final SessionService instance = new SessionService();

    public static SessionService instance() {
        return instance;
    }

    public ServiceResult addSession(String conferenceId, JsonObject incomingJson) {
        JsonObject talkData = incomingJson.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject());
        JsonArray speakers = incomingJson.arrayValue(SessionVariables.SPEAKER_ARRAY).orElse(JsonFactory.jsonArray());
        Optional<String> postedBy = incomingJson.stringValue(SessionVariables.POSTED_BY_MAIL);

        CreateNewSession createNewSession = new CreateNewSession();
        createNewSession.setConferenceId(conferenceId);
        createNewSession.setPostedByMail(postedBy);
        createNewSession.setSessionStatus(
                incomingJson.stringValue(SessionVariables.SESSION_STATUS)
                .map(SessionStatus::valueOf));

        speakers.objects(speakobj -> {
            NewSpeaker newSpeaker = new NewSpeaker();
            newSpeaker.setName(speakobj.stringValue("name"));
            newSpeaker.setEmail(speakobj.stringValue("email"));
            addData(speakobj.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject()), newSpeaker);
            return newSpeaker;
        }).forEach(createNewSession::addSpeaker);


        addData(talkData, createNewSession);
        Event event = createNewSession.createEvent();
        EventHandler.instance().addEvent(event);

        return ServiceResult.ok(JsonFactory.jsonObject().put("id", createNewSession.getSessionId()));
    }

    private void addData(JsonObject talkData, HasDataInput hasDataInput) {
        for (String key : talkData.keys()) {
            JsonObject valueObject = talkData.requiredObject(key);
            JsonNode jsonValue = valueObject.value(SessionVariables.VALUE_KEY).orElse(new JsonNull());
            boolean privateValue = valueObject.booleanValue(SessionVariables.PRIVATE_FLAG).orElse(false);
            hasDataInput.addData(key, new DataField(jsonValue, privateValue));
        }
    }

    public ServiceResult sessionById(String sessionId) {
        Optional<Session> session = SessionHolder.instance().sessionFromId(sessionId);
        if (!session.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown sessionid " + sessionId);
        }
        return ServiceResult.ok(session.get().asSingleSessionJson());
    }

    public ServiceResult updateSession(String sessionId, JsonObject payload) {
        Optional<Session> session = SessionHolder.instance().sessionFromId(sessionId);
        if (!session.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown sessionid " + sessionId);
        }
        JsonObject talkData = payload.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject());

        UpdateSession updateSession = new UpdateSession(sessionId, session.get().getConferenceId());
        addData(talkData, updateSession);
        payload.stringValue(SessionVariables.SESSION_STATUS)
                .map(SessionStatus::valueOf)
                .ifPresent(updateSession::setSessionStatus);

        Event event = updateSession.createEvent();
        EventHandler.instance().addEvent(event);

        return ServiceResult.ok(JsonFactory.jsonObject());
    }


    public ServiceResult allSessionsForConference(String conferenceId) {
        List<Session> sessions;
        if (conferenceId == null) {
            sessions = new ArrayList<>();
        } else {
           sessions = ServiceLocator.sessionHolder().allSessions().stream()
                    .filter(session -> conferenceId.equals(session.getConferenceId()))
                    .collect(Collectors.toList());

        }

        JsonObject result = JsonFactory.jsonObject().put("sessions", JsonArray.fromNodeStream(sessions.stream().map(Session::asSingleSessionJson)));
        return ServiceResult.ok(result);
    }

}
