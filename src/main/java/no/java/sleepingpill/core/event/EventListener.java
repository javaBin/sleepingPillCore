package no.java.sleepingpill.core.event;

public interface EventListener {
    default void sagaInitialized() {
    }
    void eventAdded(Event event);
}
