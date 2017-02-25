package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.SessionChangedException;
import no.java.sleepingpill.core.session.*;
import org.jsonbuddy.*;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.*;

public class UpdateSession implements HasDataInput {
    private final String sessionId;
    private final String conferenceId;
    private final Map<String, DataField> data = new HashMap<>();
    private Optional<SessionStatus> sessionStatus = Optional.empty();
    private Optional<List<SpeakerData>> speakers = Optional.empty();
    private Optional<String> lastUpdated = Optional.empty();

    public UpdateSession(String sessionId, String conferenceId) {
        this.sessionId = sessionId;
        this.conferenceId = conferenceId;
    }

    public UpdateSession addData(String key, DataField dataField) {
        data.put(key, dataField);
        return this;
    }

    public UpdateSession addSpeakerData(SpeakerData speaker) {
        this.speakers = Optional.of(this.speakers.orElse(new ArrayList<>()));
        this.speakers.get().add(speaker);
        return this;
    }

    public Event createEvent(Session session) throws SessionChangedException {
        checkLastUpdated(session);
        JsonObject jsonObject = JsonFactory.jsonObject()
                .put(SessionVariables.DATA_OBJECT, JsonGenerator.generate(data))
                .put(SessionVariables.SESSION_ID, sessionId)
                ;
        speakers.ifPresent(sparr ->
                jsonObject.put(SessionVariables.SPEAKER_ARRAY,
                        JsonArray.fromNodeStream(sparr.stream().map(SpeakerData::eventData))))
                ;
        sessionStatus.ifPresent(status -> jsonObject.put(SessionVariables.SESSION_STATUS,status.toString()));
        Event event = new Event(EventType.UPDATE_SESSION, jsonObject,Optional.of(conferenceId));
        return event;
    }

    private void checkLastUpdated(Session session) throws SessionChangedException {
        if (!lastUpdated.isPresent()) {
            return;
        }
        if (!lastUpdated.get().equals(session.getLastUpdated())) {
            throw new SessionChangedException(session.getLastUpdated(),lastUpdated.get());
        }
    }

    public UpdateSession setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = Optional.of(sessionStatus);
        return this;
    }

    public static UpdateSession fromInputJson(JsonObject payload, Session session) {
        UpdateSession updateSession = new UpdateSession(session.getId(), session.getConferenceId());
        JsonObject talkData = payload.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject());
        addData(talkData, updateSession);
        payload.stringValue(SessionVariables.SESSION_STATUS)
                .map(SessionStatus::valueOf)
                .ifPresent(updateSession::setSessionStatus);
        payload.arrayValue(SessionVariables.SPEAKER_ARRAY).orElse(JsonFactory.jsonArray())
            .objectStream()
            .map(SpeakerData::fromJson)
            .forEach(updateSession::addSpeakerData);
        updateSession.lastUpdated = payload.stringValue(SessionVariables.LAST_UPDATED);
        return updateSession;

    }

    private static void addData(JsonObject talkData, HasDataInput hasDataInput) {
        for (String key : talkData.keys()) {
            JsonObject valueObject = talkData.requiredObject(key);
            JsonNode jsonValue = valueObject.value(SessionVariables.VALUE_KEY).orElse(new JsonNull());
            boolean privateValue = valueObject.booleanValue(SessionVariables.PRIVATE_FLAG).orElse(false);
            hasDataInput.addData(key, new DataField(jsonValue, privateValue));
        }
    }

    public UpdateSession setLastUpdated(String lastUpdated) {
        this.lastUpdated = Optional.of(lastUpdated);
        return this;
    }
}
