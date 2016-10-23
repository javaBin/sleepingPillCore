package no.java.sleepingpill.core.event;

import no.java.sleepingpill.core.ServiceLocator;

import java.util.List;

public interface ArrangedEventHolder {
    List<ArrangedEvent> allArrangedEvents();

    public static ArrangedEventHolder instance() {
        return ServiceLocator.arrangedEventHolder();
    }
}
