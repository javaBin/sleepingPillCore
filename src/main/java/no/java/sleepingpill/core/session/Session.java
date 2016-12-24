package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;

import java.util.*;
import java.util.stream.Stream;

public class Session extends DataObject {
    private final String conferenceId;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;
    private List<Speaker> speakers = new ArrayList<>();

    public Session(String id, String conferenceId) {
        super(id);
        this.conferenceId = conferenceId;
    }


    @Override
    public Map<String, DataField> getData() {
        return super.getData();
    }

    @Override // Needed for json generation
    public String getId() {
        return super.getId();
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public JsonObject asSingleSessionJson() {
        JsonObject result = JsonFactory.jsonObject()
                .put("id", getId())
                .put("speakers", JsonArray.fromNodeStream(speakers.stream().map(Speaker::singleSessionData)))
                .put("data", dataAsJson())
                .put("status",sessionStatus)
                ;
        return result;
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

    public Session setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
        return this;
    }
}
