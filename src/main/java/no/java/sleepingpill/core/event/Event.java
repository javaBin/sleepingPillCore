package no.java.sleepingpill.core.event;

import org.jsonbuddy.JsonObject;

public class Event {
    public final EventType eventType;
    public final long index;
    public final JsonObject data;

    public Event(EventType eventType, long index, JsonObject data) {
        this.eventType = eventType;
        this.index = index;
        this.data = data;
    }

    public Event(EventType eventType, JsonObject data) {
        this(eventType, EventHandler.nextId(), data);

    }
}
