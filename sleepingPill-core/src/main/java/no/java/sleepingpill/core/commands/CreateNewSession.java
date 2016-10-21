package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.session.DataField;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.*;

public class CreateNewSession {
    private Optional<String> sessionId = Optional.empty();
    private String arrangedEventId;
    private List<NewSpeaker> speakers = new ArrayList<>();
    private Map<String,DataField> data = new HashMap<>();

    public CreateNewSession setSessionId(String sessionId) {
        this.sessionId = Optional.of(sessionId);
        return this;
    }

    public CreateNewSession setArrangedEventId(String arrangedEventId) {
        this.arrangedEventId = arrangedEventId;
        return this;
    }

    public void addSpeaker(NewSpeaker speaker) {
        speakers.add(speaker);
    }

    public CreateNewSession addData(String key,DataField dataField) {
        data.put(key,dataField);
        return this;
    }

    public Event createEvent() {
        this.sessionId = Optional.of(sessionId.orElse(UUID.randomUUID().toString()));
        JsonObject dataObj = JsonFactory.jsonObject();
        dataObj.put("sessionId",sessionId.get());
        dataObj.put("speakers", JsonArray.fromNodeStream(speakers.stream().map(NewSpeaker::asNewEvent)));
        dataObj.put("data", JsonGenerator.generate(data));
        return new Event(EventType.NEW_SESSION,arrangedEventId,dataObj);
    }

    public String getSessionId() {
        return sessionId.orElseThrow(() -> new InternalError("Session Id not set yet"));
    }
}
