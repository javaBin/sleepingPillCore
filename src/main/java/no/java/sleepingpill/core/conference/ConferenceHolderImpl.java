package no.java.sleepingpill.core.conference;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.event.EventType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConferenceHolderImpl implements ConferenceHolder {
    private ConcurrentMap<String,Conference> conferences = new ConcurrentHashMap<>();

    @Override
    public List<Conference> allConferences() {
        List<Conference> result = new ArrayList<>(this.conferences.values());
        result.sort(Comparator.comparing(Conference::getName));
        return result;
    }

    @Override
    public void eventAdded(Event event) {
        if (event.eventType == EventType.NEW_CONFERENCE) {
            Conference conference = new Conference(event.data.requiredString(ConferenceVariables.CONFERENCE_ID), event.data.requiredString(ConferenceVariables.CONFERENCE_NAME), event.data.requiredString(ConferenceVariables.CONFERENCE_SLUG));
            conferences.put(conference.id,conference);
            return;
        }
        if (event.eventType == EventType.UPDATE_CONFERENCE) {
            Conference conference = conferences.get(event.data.requiredString(ConferenceVariables.CONFERENCE_ID));
            conference.setName(event.data.requiredString(ConferenceVariables.CONFERENCE_NAME));
        }
    }
}
