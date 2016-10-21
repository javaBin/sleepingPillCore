package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.event.EventType;

import java.util.ArrayList;
import java.util.List;

public class SessionHolder implements EventListener {
    private final static SessionHolder _instance = new SessionHolder();

    public static SessionHolder instance() {
        return _instance;
    }

    private List<Session> allSessions = new ArrayList<>();

    @Override
    public void eventAdded(Event event) {
        if (event.eventType == EventType.NEW_SESSION) {
            handleNewSession(event);
        }
    }

    private void handleNewSession(Event event) {
        String sessionId = event.data.requiredString("sessionId");
        Session session = new Session(sessionId, event.arrangedEventId);
        session.addData(event.data);
        allSessions.add(session);
    }

    public List<Session> allSessions() {
        return new ArrayList<>(allSessions);
    }
}
