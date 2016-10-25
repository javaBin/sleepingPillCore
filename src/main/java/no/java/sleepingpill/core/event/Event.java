package no.java.sleepingpill.core.event;

import org.jsonbuddy.JsonObject;

public class Event {
    public final EventType eventType;
    public final String arrangedEventId;
    public final long index;
    public final JsonObject data;

    public Event(EventType eventType, String arrangedEventId, long index, JsonObject data) {
        this.eventType = eventType;
        this.arrangedEventId = arrangedEventId;
        this.index = index;
        this.data = data;
    }

    public Event(EventType eventType, String arrangedEventId, JsonObject data) {
        this(eventType,arrangedEventId,EventHandler.nextId(),data);

    }
}
