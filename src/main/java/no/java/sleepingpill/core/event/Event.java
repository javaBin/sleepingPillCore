package no.java.sleepingpill.core.event;

import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class Event {
    public final EventType eventType;
    public final long index;
    public final JsonObject data;
    public final Optional<String> conferenceId;

    public Event(EventType eventType, long index, JsonObject data,Optional<String> conferenceId) {
        this.eventType = eventType;
        this.index = index;
        this.data = data;
        this.conferenceId = conferenceId;
    }

    public Event(EventType eventType, JsonObject data,Optional<String> conferenceId) {
        this(eventType, EventHandler.nextId(), data,conferenceId);

    }
}
