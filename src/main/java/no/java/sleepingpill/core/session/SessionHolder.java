package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.InternalError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionHolder implements EventListener {

    private List<Session> sessions = new ArrayList<>();

    public static SessionHolder instance() {
        return ServiceLocator.sessionHolder();
    }

    @Override
    public void eventAdded(Event event) {
        if (event.eventType == EventType.NEW_SESSION) {
            handleNewSession(event);
        }
        if (event.eventType == EventType.UPDATE_SESSION) {
            handleUpdateSession(event);
        }
    }

    public void clear() {
        sessions.clear();
        ;
    }

    private void handleUpdateSession(Event event) {
        String sessionId = event.data.requiredString("sessionId");
        Session session = sessions.stream()
                .filter(se -> se.getId().equals(sessionId))
                .findAny()
                .orElseThrow(() -> new InternalError("Unknown session id in update " + sessionId));
        session.addData(event.data);

    }

    private void handleNewSession(Event event) {
        String sessionId = event.data.requiredString("sessionId");
        String conferenceId = event.data.requiredString("conferenceId");
        Session session = new Session(sessionId, conferenceId);
        session.addData(event.data);
        sessions.add(session);
    }

    public List<Session> allSessions() {
        return new ArrayList<>(sessions);
    }

    public Optional<Session> sessionFromId(String sessionId) {
        return sessions.stream()
                .filter(se -> se.getId().equals(sessionId))
                .findAny();
    }
}
