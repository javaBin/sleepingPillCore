package no.java.sleepingpill.core.conference;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.event.EventListener;

import java.util.List;

public interface ConferenceHolder extends EventListener {
    static ConferenceHolder instance() {
        return ServiceLocator.conferenceHolder();
    }

    List<Conference> allConferences();
}
