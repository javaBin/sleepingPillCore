package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.Talk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmsDao extends AutoCloseable {
    void addEvent(Event event);
    Optional<Event> findEvent(String eventid);
    List<Event> allEvents();

    void addTalk(Talk talk);
    void updateTalk(Talk talk);
    Optional<Talk> findTalk(String talkid);
    List<Talk> allTalks(String eventid);

    void rollback();

    default String newKey() {
        return UUID.randomUUID().toString();
    }
}
