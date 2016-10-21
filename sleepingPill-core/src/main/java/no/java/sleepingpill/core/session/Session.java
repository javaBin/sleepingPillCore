package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Session extends DataObject {
    private final String arrangedEventId;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;

    public Session(String id, String arrangedEventId) {
        super(id);
        this.arrangedEventId = arrangedEventId;
    }



}
