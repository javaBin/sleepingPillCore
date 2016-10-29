package no.java.sleepingpill.core.event;

import no.java.sleepingpill.core.ServiceLocator;

import java.util.List;

public interface ConferenceHolder {
    static ConferenceHolder instance() {
        return ServiceLocator.conferenceHolder();
    }

    List<Conference> allConferences();
}
