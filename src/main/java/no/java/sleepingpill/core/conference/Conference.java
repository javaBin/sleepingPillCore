package no.java.sleepingpill.core.conference;

public class Conference {
    public final String id;
    private volatile String name;
    public final String slug;

    public Conference(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public Conference setName(String name) {
        this.name = name;
        return this;
    }
}
