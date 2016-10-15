package no.java.sleepingpill.core.event;

public class ArrangedEvent {
    public final String id;
    public final String name;
    public final String slug;

    public ArrangedEvent(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
}
