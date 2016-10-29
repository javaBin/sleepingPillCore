package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.*;

public class CreateNewSession implements HasDataInput {
    private Optional<String> sessionId = Optional.empty();
    private String conferenceId;
    private List<NewSpeaker> speakers = new ArrayList<>();
    private Map<String, DataField> data = new HashMap<>();
    private Optional<String> postedByMail = Optional.empty();

    public CreateNewSession setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
        return this;
    }

    public CreateNewSession setPostedByMail(Optional<String> postedByMail) {
        this.postedByMail = postedByMail;
        return this;
    }

    public void addSpeaker(NewSpeaker speaker) {
        speakers.add(speaker);
    }

    public void addData(String key, DataField dataField) {
        data.put(key, dataField);
    }

    public Event createEvent() {
        this.sessionId = Optional.of(sessionId.orElse(IdGenerator.newId()));
        JsonObject dataObj = JsonFactory.jsonObject();
        dataObj.put("conferenceId", conferenceId);
        dataObj.put("sessionId", sessionId.get());
        dataObj.put("speakers", JsonArray.fromNodeStream(speakers.stream().map(NewSpeaker::asNewEvent)));
        dataObj.put("data", JsonGenerator.generate(data));
        if (postedByMail.isPresent()) {
            dataObj.put("postedByMail", postedByMail.get());
        }

        return new Event(EventType.NEW_SESSION, dataObj);
    }

    public String getSessionId() {
        return sessionId.orElseThrow(() -> new InternalError("Session Id not set yet"));
    }

    public CreateNewSession setSessionId(String sessionId) {
        this.sessionId = Optional.of(sessionId);
        return this;
    }
}
