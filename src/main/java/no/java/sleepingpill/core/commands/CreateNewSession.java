package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionStatus;
import no.java.sleepingpill.core.session.SessionVariables;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.*;

public class CreateNewSession implements HasDataInput {
    private Optional<String> sessionId = Optional.empty();
    private String conferenceId;
    private List<SpeakerData> speakers = new ArrayList<>();
    private Map<String, DataField> data = new HashMap<>();
    private Optional<String> postedByMail = Optional.empty();
    private Optional<SessionStatus> sessionStatus = Optional.empty();

    public CreateNewSession setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
        return this;
    }

    public CreateNewSession setPostedByMail(Optional<String> postedByMail) {
        this.postedByMail = postedByMail;
        return this;
    }

    public CreateNewSession addSpeaker(SpeakerData speaker) {
        speakers.add(speaker);
        return this;
    }

    public CreateNewSession addData(String key, DataField dataField) {
        data.put(key, dataField);
        return this;
    }

    public Event createEvent() {
        this.sessionId = Optional.of(sessionId.orElse(IdGenerator.newId()));
        JsonObject dataObj = JsonFactory.jsonObject();
        dataObj.put("conferenceId", conferenceId);
        dataObj.put(SessionVariables.SESSION_ID, sessionId.get());
        dataObj.put(SessionVariables.SPEAKER_ARRAY, JsonArray.fromNodeStream(speakers.stream().map(SpeakerData::eventData)));
        dataObj.put(SessionVariables.DATA_OBJECT, JsonGenerator.generate(data));
        postedByMail.ifPresent(s -> dataObj.put(SessionVariables.POSTED_BY_MAIL, s));
        sessionStatus.ifPresent(status -> dataObj.put(SessionVariables.SESSION_STATUS,status.toString()));

        return new Event(EventType.NEW_SESSION, dataObj,Optional.of(conferenceId));
    }

    public String getSessionId() {
        return sessionId.orElseThrow(() -> new InternalError("Session Id not set yet"));
    }

    public CreateNewSession setSessionId(String sessionId) {
        this.sessionId = Optional.of(sessionId);
        return this;
    }

    public CreateNewSession setSessionStatus(Optional<SessionStatus> sessionStatus) {
        this.sessionStatus = sessionStatus;
        return this;
    }
}
