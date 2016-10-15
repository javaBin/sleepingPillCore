package no.java.sleepingpill.core.event;

public class EventHandler {
    private static final EventHandler _instance = new EventHandler();



    public static EventHandler instance() {
        return _instance;
    }

    private EventHandler() {
    }

    public synchronized void addEvent(Event event) {

    }

    public synchronized void addEventListener(EventListener eventListener) {

    }
}
