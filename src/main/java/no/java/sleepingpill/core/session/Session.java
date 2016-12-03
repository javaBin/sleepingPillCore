package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.*;

public class Session extends DataObject {
    private final String conferenceId;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;
    private List<Speaker> speakers = new ArrayList<>();

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

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    @Override
    public void addData(JsonObject update) {
        super.addData(update);
        Optional<JsonArray> optSpeaker = update.arrayValue(SessionService.SPEAKER_ARRAY);
        optSpeaker.ifPresent(jsonNodes -> jsonNodes.objectStream().forEach(jsp -> speakers.add(Speaker.fromJson(getId(), jsp))));

    }

    public List<Speaker> getSpeakers() {
        return new ArrayList<>(speakers);
    }
}
