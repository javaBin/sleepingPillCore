package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionStatus;
import no.java.sleepingpill.core.session.SessionVariables;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpdateSession implements HasDataInput {
    private final String sessionId;
    private final String conferenceId;
    private final Map<String, DataField> data = new HashMap<>();
    private Optional<SessionStatus> sessionStatus = Optional.empty();

    public UpdateSession(String sessionId, String conferenceId) {
        this.sessionId = sessionId;
        this.conferenceId = conferenceId;
    }

    public void addData(String key, DataField dataField) {
        data.put(key, dataField);
    }

    public Event createEvent() {
        JsonObject jsonObject = JsonFactory.jsonObject()
                .put(SessionVariables.DATA_OBJECT, JsonGenerator.generate(data))
                .put("sessionId", sessionId);
        sessionStatus.ifPresent(status -> jsonObject.put(SessionVariables.SESSION_STATUS,status.toString()));
        Event event = new Event(EventType.UPDATE_SESSION, jsonObject);
        return event;
    }

    public UpdateSession setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = Optional.of(sessionStatus);
        return this;
    }
}
