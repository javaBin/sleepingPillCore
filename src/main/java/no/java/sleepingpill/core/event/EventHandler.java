package no.java.sleepingpill.core.event;

import no.java.sleepingpill.core.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class EventHandler {
    private static final EventHandler _instance = new EventHandler();
    private List<EventListener> listeners = new ArrayList<>();

    private static final AtomicLong idGenerator = new AtomicLong(0);

    public static long nextId() {
        return idGenerator.incrementAndGet();
    }

    public static EventHandler instance() {
        return ServiceLocator.eventHandler();
    }


    public synchronized void addEvent(Event event) {
        for (EventListener eventListener : listeners) {
            eventListener.eventAdded(event);
        }
    }

    public synchronized void addEventListener(EventListener eventListener) {
        listeners.add(eventListener);
    }

    public List<EventListener> getEventListeners() {
        return new ArrayList<>(listeners);
    }
}
