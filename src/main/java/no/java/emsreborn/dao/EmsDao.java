package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.Talk;

import java.util.List;
import java.util.Optional;

public interface EmsDao {
    void addEvent(Event event);
    Optional<Event> findEvent(String eventid);
    List<Event> allEvents();

    void addTalk(Talk talk);
    void updateTalk(Talk talk);
    Optional<Talk> findTalk(String talkid);
    List<Talk> allTalks(String eventid);
}
