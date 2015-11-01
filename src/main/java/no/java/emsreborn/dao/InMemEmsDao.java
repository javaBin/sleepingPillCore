package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.Talk;

import java.util.*;

public class InMemEmsDao implements EmsDao {
    private static final Map<String,Event> events = new HashMap<>();

    @Override
    public void addEvent(Event event) {
        events.put(event.eventid,event);
    }

    @Override
    public Optional<Event> findEvent(String eventid) {
        return Optional.ofNullable(events.get(eventid));
    }

    @Override
    public List<Event> allEvents() {
        return new ArrayList<>(events.values());
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
}
