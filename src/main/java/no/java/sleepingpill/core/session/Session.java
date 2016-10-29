package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Session extends DataObject {
    private final String conferenceId;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;

    public Session(String id, String conferenceId) {
        super(id);
        this.conferenceId = conferenceId;
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public Map<String, DataField> getData() {
        return super.getData();
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public JsonObject asSingleSessionJson() {
        return dataAsJson();
    }
}
