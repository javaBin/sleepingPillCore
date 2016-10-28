package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonFactory;
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

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public Map<String, DataField> getData() {
        return super.getData();
    }

    public String getArrangedEventId() {
        return arrangedEventId;
    }

    public JsonObject asSingleSessionJson() {
        return dataAsJson();
    }
}
