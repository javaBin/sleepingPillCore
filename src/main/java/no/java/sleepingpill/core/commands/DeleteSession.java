package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.session.SessionVariables;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class DeleteSession {
    private final String conferenceid;
    private final String sessionid;

    public DeleteSession(String conferenceid, String sessionid) {
        this.conferenceid = conferenceid;
        this.sessionid = sessionid;
    }

    public Event createEvent() {
        JsonObject payload = JsonFactory.jsonObject().put(SessionVariables.SESSION_ID,sessionid);
        return new Event(EventType.DELETE_SESSION,payload, Optional.of(conferenceid));
    }
}
