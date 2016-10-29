package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.session.DataField;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.HashMap;
import java.util.Map;

public class UpdateSession implements HasDataInput {
    private final String sessionId;
    private final String conferenceId;
    private final Map<String, DataField> data = new HashMap<>();

    public UpdateSession(String sessionId, String conferenceId) {
        this.sessionId = sessionId;
        this.conferenceId = conferenceId;
    }

    public void addData(String key, DataField dataField) {
        data.put(key, dataField);
    }

    public Event createEvent() {
        JsonObject jsonObject = JsonFactory.jsonObject()
                .put("data", JsonGenerator.generate(data))
                .put("sessionId", sessionId);
        Event event = new Event(EventType.UPDATE_SESSION, jsonObject);
        return event;
    }
}
