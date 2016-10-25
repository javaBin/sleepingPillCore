package no.java.sleepingpill.core.session;

public class Speaker extends DataObject {
    private final String sessionId;

    public Speaker(String id, String sessionId) {
        super(id);
        this.sessionId = sessionId;
    }
}
