package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.util.DateUtil;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.time.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static no.java.sleepingpill.core.session.SessionVariables.*;

public class Session extends DataObject {
    private final String conferenceId;
    private final Optional<String> addedByEmail;
    private volatile String lastUpdated;
    private volatile SessionStatus sessionStatus = SessionStatus.DRAFT;
    private volatile List<Speaker> speakers = new CopyOnWriteArrayList<>();
    private volatile List<Comment> comments = new CopyOnWriteArrayList<>();
    private volatile Optional<LocalDateTime> submittedTime = Optional.empty();
    private volatile Optional<Session> publicVersion = Optional.empty();
    private volatile SessionUpdates sessionUpdates = SessionUpdates.noUpdates();

    public Session(String id, String conferenceId,Optional<String> addedByEmail) {
        super(id);
        this.conferenceId = conferenceId;
        this.addedByEmail = addedByEmail;
    }

    private Session(Session session) {
        super(session);
        this.conferenceId = session.conferenceId;
        this.addedByEmail = session.addedByEmail;
        this.lastUpdated = session.lastUpdated;
        this.sessionStatus = session.sessionStatus;
        this.speakers = new CopyOnWriteArrayList<>(session.speakers.stream().map(Speaker::new).collect(Collectors.toList()));
        this.comments = new CopyOnWriteArrayList<>(session.comments.stream().map(Comment::new).collect(Collectors.toList()));
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
                .put(SESSION_ID, getId())
                .put(SPEAKER_ARRAY, JsonArray.fromNodeStream(speakers.stream().map(Speaker::singleSessionData)))
                .put(DATA_OBJECT, dataAsJson())
                .put(SESSION_STATUS,sessionStatus)
                .put(CONFERENCE_ID,conferenceId)
                .put(LAST_UPDATED,lastUpdated)
                .put(COMMENT_ARRAY,JsonArray.fromNodeStream(comments.stream().map(Comment::toJson)))
                .put(SESSION_UPDATES, JsonGenerator.generate(getSessionUpdates()))
                ;
        submittedTime.ifPresent(time -> result.put(SUBMITTED_TIME,time.toString()));
        addedByEmail.ifPresent(mail -> result.put(SessionVariables.POSTED_BY_MAIL,mail));
        return result;
    }

    public boolean isPublic() {
        return Optional.ofNullable(sessionStatus).map(SessionStatus::isPublic).orElse(false);
    }

    public JsonObject asPublicSessionJson() {
        if (!isPublic()) {
            throw new RuntimeException("Tried to handle private session as public");
        }
        if (publicVersion.isPresent()) {
            return publicVersion.get().asPublicSessionJson();
        }
        JsonObject result = JsonFactory.jsonObject();
        result.put(SESSION_ID,publicSessionId());
        result.put(CONFERENCE_ID,conferenceId);
        Map<String, DataField> data = getData();
        for (String key : data.keySet()) {
            data.get(key).readPublicData().ifPresent(da -> result.put(key,da));
        }
        addSlotTimesWithZuluTime(Optional.ofNullable(data.get(SessionVariables.START_TIME)),result,"startTimeZulu");
        addSlotTimesWithZuluTime(Optional.ofNullable(data.get(SessionVariables.END_TIME)),result,"endTimeZulu");
        result.put(SPEAKER_ARRAY,JsonArray.fromNodeStream(speakers.stream().map(Speaker::asPublicJson)));
        return result;
    }

    private String publicSessionId() {
        return dataValue(EMS_LOCATION)
                .map(DataField::propertyValue)
                .map(emsloc -> emsloc.substring(emsloc.lastIndexOf("/")+1))
                .orElse(getId());

    }

    private static void addSlotTimesWithZuluTime(Optional<DataField> dataField, JsonObject dataObj, String fieldName) {
        if (!dataField.isPresent()) {
            return;
        }
        LocalDateTime localdate = LocalDateTime.parse(dataField.get().propertyValue());
        dataObj.put(fieldName, DateUtil.toZuluTimeString(localdate));
    }


    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    @Override
    public void addData(JsonObject update) {
        SessionStatus newSessStatus = update.stringValue(SessionVariables.SESSION_STATUS)
                .map(SessionStatus::valueOf)
                .orElse(this.sessionStatus);
        boolean wasPublised = newSessStatus.wasPublished(this.sessionStatus);
        super.addData(update);
        Optional<JsonArray> optSpeaker = update.arrayValue(SPEAKER_ARRAY);
        optSpeaker.ifPresent(this::updateSpeakers);
        update.arrayValue(COMMENT_ARRAY).ifPresent(this::updateComments);
        this.lastUpdated = update.stringValue(LAST_UPDATED).orElse(DateUtil.get().generateLastUpdated());
        this.sessionStatus = newSessStatus;
        if (wasPublised) {
            this.publicVersion = Optional.of(new Session(this));
            this.sessionUpdates = SessionUpdates.noUpdates();
        } else {
            this.sessionUpdates = computeSessionUpdates();
        }
    }

    public void publishChanges() {
        if (!getSessionUpdates().getHasUnpublishedChanges()) {
            return;
        }
        this.publicVersion = Optional.of(new Session(this));
        this.sessionUpdates = SessionUpdates.noUpdates();
    }

    private SessionUpdates computeSessionUpdates() {
        if (!this.publicVersion.isPresent()) {
            return SessionUpdates.noUpdates();
        }
        Session pver = this.publicVersion.get();
        Map<String, String> changedMap = super.changedPublicFields(pver);

        Set<String> currentSpeakers = new HashSet<>();
        List<SpeakerUpdates> speakerUpdates = new ArrayList<>();
        for (Speaker speaker : speakers) {
            currentSpeakers.add(speaker.getId());
            Optional<Speaker> matching = pver.speakers.stream()
                    .filter(sp -> sp.getId().equals(speaker.getId()))
                    .findAny();
            if (!matching.isPresent()) {
                speakerUpdates.add(new SpeakerUpdates(speaker.getId(),allPublic(speaker),UpdateType.ADDED));
                continue;
            }
            Map<String, String> changedPublicFieldsMap = speaker.changedPublicFields(matching.get());
            if (changedPublicFieldsMap.isEmpty()) {
                continue;
            }
            List<JsonObject> changedPublicFields = changedPublicFieldsMap.entrySet().stream()
                    .map(en -> JsonFactory.jsonObject().put("key",en.getKey()).put("value",en.getValue()))
                    .collect(Collectors.toList());
            speakerUpdates.add(new SpeakerUpdates(speaker.getId(),changedPublicFields,UpdateType.CHANGED));
        }
        for (Speaker speaker : pver.speakers) {
            if (currentSpeakers.contains(speaker.getId())) {
                continue;
            }
            speakerUpdates.add(new SpeakerUpdates(speaker.getId(),allPublic(speaker),UpdateType.DELETED));
        }

        List<JsonObject> changes = changedMap.entrySet().stream()
                .map(entr -> JsonFactory.jsonObject().put("key", entr.getKey()).put("value", entr.getValue()))
                .collect(Collectors.toList());

        return new SessionUpdates(changes,speakerUpdates);
    }

    private static List<JsonObject> allPublic(Speaker speaker) {
        Map<String, String> result = new HashMap<>();
        speaker.getData().entrySet().stream()
                .filter(en -> !en.getValue().isPrivateData())
                .filter(en -> en.getValue().isProperty())
                .forEach(en -> result.put(en.getKey(),en.getValue().propertyValue()));
        result.put("name",speaker.getName());
        return result.entrySet().stream()
                .map(en -> JsonFactory.jsonObject().put("key",en.getKey()).put("value",en.getValue()))
                .collect(Collectors.toList());
    }

    private void updateComments(JsonArray updatedCommentsJson) {
        updatedCommentsJson.objectStream()
                .map(Comment::fromJson)
                .forEach(this.comments::add);
    }

    private void updateSpeakers(JsonArray updatedSpeakersJson) {
        List<Speaker> updatedSpeakers = new ArrayList<>();

        for (Speaker exisisting : this.speakers) {
            Optional<JsonObject> updateOnSpeaker = updatedSpeakersJson.objectStream()
                    .filter(ob -> ob.objectValue("id").orElse(JsonFactory.jsonObject()).stringValue("value").equals(Optional.of(exisisting.getId())))
                    .findAny();
            updateOnSpeaker.map(exisisting::update).ifPresent(updatedSpeakers::add);
        }

        updatedSpeakersJson.objectStream()
                .filter(ob -> speakerExists(updatedSpeakers, ob))
                .forEach(ob -> updatedSpeakers.add(Speaker.fromJson(getId(),ob)));


        this.speakers = updatedSpeakers;
    }

    private boolean speakerExists(List<Speaker> updatedSpeakers, JsonObject ob) {
        Optional<String> id = ob.objectValue("id").map(vo -> vo.stringValue("value").orElse(null));
        return !updatedSpeakers.stream().filter(speak -> Optional.of(speak.getId()).equals(id)).findAny().isPresent();
    }

    public List<Speaker> getSpeakers() {
        return new ArrayList<>(speakers);
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(getId(), session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Session setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public Session setSubmittedTime(long millis) {
        if (submittedTime.isPresent()) {
            return this;
        }
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        submittedTime = Optional.of(date);
        return this;
    }

    public SessionUpdates getSessionUpdates() {
        return sessionUpdates;
    }


}
