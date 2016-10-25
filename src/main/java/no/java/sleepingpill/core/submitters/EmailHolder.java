package no.java.sleepingpill.core.submitters;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.event.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmailHolder implements EventListener {
    public static EmailHolder instance() {
        return ServiceLocator.emailHolder();
    }

    private volatile List<ConfirmedEmail> confirmedEmailList = new ArrayList<>();

    @Override
    public void eventAdded(Event event) {
        if (event.eventType != EventType.EMAIL_CONFIRMED) {
            return;
        }
        confirmedEmailList.add(new ConfirmedEmail(event.data.requiredString("id"),event.data.requiredString("email")));
    }

    public Optional<ConfirmedEmail> confirmedEmailByEmail(String email) {
        return confirmedEmailList.stream()
                .filter(ce -> ce.email.equals(email))
                .findAny();
    }

    public Optional<ConfirmedEmail> confirmedEmailById(String id) {
        return confirmedEmailList.stream()
                .filter(ce -> ce.id.equals(id))
                .findAny();
    }


}
