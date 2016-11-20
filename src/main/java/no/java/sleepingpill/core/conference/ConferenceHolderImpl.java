package no.java.sleepingpill.core.conference;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.event.EventType;

import java.util.ArrayList;
import java.util.List;

public class ConferenceHolderImpl implements ConferenceHolder,EventListener {
    private List<Conference> conferences = new ArrayList<>();

    @Override
    public List<Conference> allConferences() {
        return conferences;
    }

    @Override
    public void eventAdded(Event event) {
        if (event.eventType != EventType.NEW_CONFERENCE) {
            return;
        }
        Conference conference = new Conference(event.data.requiredString("id"), event.data.requiredString("name"), event.data.requiredString("slug"));
        conferences.add(conference);
    }
}
