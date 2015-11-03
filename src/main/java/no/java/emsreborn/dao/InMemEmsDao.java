package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.Talk;

import java.util.*;

public class InMemEmsDao implements EmsDao {
    private static final Map<String,Event> allEvents = new HashMap<>();
    private static final Map<String,Talk> allTalks = new HashMap<>();

    private final Map<String,Event> updatedEvents = new HashMap();
    private final Map<String,Talk> updatedTalks = new HashMap();

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

    private Map<String, Talk> talks() {
        Map<String, Talk> computedTalks = new HashMap<>();
        computedTalks.putAll(allTalks);
        computedTalks.putAll(updatedTalks);
        return computedTalks;
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
        updatedTalks.put(talk.getTalkid(),talk);
    }

    @Override
    public void updateTalk(Talk talk) {
        throw new UnsupportedOperationException("updateTalk");
    }

    @Override
    public Optional<Talk> findTalk(String talkid) {
        return talks().values().stream()
                .filter(talk -> talk.getTalkid().equals(talkid))
                .findAny();
    }

    @Override
    public List<Talk> allTalks(String eventid) {
        return new ArrayList<>(talks().values());
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
