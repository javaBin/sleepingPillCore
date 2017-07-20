package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.session.SessionVariables;
import org.jsonbuddy.JsonFactory;

import java.util.Optional;

public class PublishChangesToSession {
    private final String sessionId;
    private final String conferenceid;

    public PublishChangesToSession(String sessionId, String conferenceid) {
        this.sessionId = sessionId;
        this.conferenceid = conferenceid;
    }

    public Event createEvent() {
        return new Event(
                EventType.PUBLISH_CHANGES_TO_SESSION,
                JsonFactory.jsonObject().put(SessionVariables.SESSION_ID,sessionId),
                Optional.of(conferenceid));
    }
}
