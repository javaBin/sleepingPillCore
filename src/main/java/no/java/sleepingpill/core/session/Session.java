package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.*;

public class Session extends DataObject {
    private final String conferenceId;
    private final Optional<String> addedByEmail;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;
    private List<Speaker> speakers = new ArrayList<>();

    public Session(String id, String conferenceId,Optional<String> addedByEmail) {
        super(id);
        this.conferenceId = conferenceId;
        this.addedByEmail = addedByEmail;
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
                .put(SessionVariables.SESSION_ID, getId())
                .put(SessionVariables.SPEAKER_ARRAY, JsonArray.fromNodeStream(speakers.stream().map(Speaker::singleSessionData)))
                .put(SessionVariables.DATA_OBJECT, dataAsJson())
                .put(SessionVariables.SESSION_STATUS,sessionStatus)
                .put(SessionVariables.CONFERENCE_ID,conferenceId)
                ;
        addedByEmail.ifPresent(mail -> result.put(SessionVariables.POSTED_BY_MAIL,mail));
        return result;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    @Override
    public void addData(JsonObject update) {
        super.addData(update);
        Optional<JsonArray> optSpeaker = update.arrayValue(SessionVariables.SPEAKER_ARRAY);
        optSpeaker.ifPresent(jsonNodes -> jsonNodes.objectStream().forEach(jsp -> speakers.add(Speaker.fromJson(getId(), jsp))));

    }

    public List<Speaker> getSpeakers() {
        return new ArrayList<>(speakers);
    }

    public Session setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
        return this;
    }

    public boolean isRelatedToEmail(String email) {
        if (email == null) {
            return false;
        }
        if (addedByEmail.filter(ab -> ab.equalsIgnoreCase(email)).isPresent()) {
            return true;
        }
        boolean match = speakers.stream()
                .anyMatch(sp -> email.equalsIgnoreCase(sp.getEmail()));
        return match;
    }
}
