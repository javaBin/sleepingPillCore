package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.Talk;

import java.util.*;

public class InMemEmsDao implements EmsDao {
    private static final Map<String,Event> allEvents = new HashMap<>();

    private final Map<String,Event> updatedEvents = new HashMap();

    @Override
    public void addEvent(Event event) {
        updatedEvents.put(event.eventid, event);
    }

    private Map<String, Event> events() {
        Map<String, Event> computedEvents = new HashMap<>();
        computedEvents.putAll(allEvents);
        computedEvents.putAll(updatedEvents);
        return computedEvents;

    }

    @Override
    public Optional<Event> findEvent(String eventid) {
        return Optional.ofNullable(events().get(eventid));
    }

    @Override
    public List<Event> allEvents() {
        return new ArrayList<>(events().values());
    }

    @Override
    public void addTalk(Talk talk) {

    }

    @Override
    public void updateTalk(Talk talk) {

    }

    @Override
    public Optional<Talk> findTalk(String talkid) {
        return null;
    }

    @Override
    public List<Talk> allTalks(String eventid) {
        return null;
    }

    @Override
    public void rollback() {
        updatedEvents.clear();

    }

    @Override
    public void close() throws Exception {
        allEvents.putAll(updatedEvents);
    }
}
