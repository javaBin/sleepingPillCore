package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.conference.ConferenceVariables;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class UpdateConference {
    private final String id;
    private final String name;

    public UpdateConference(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Event createEvent() {
        JsonObject dataObj = JsonFactory.jsonObject();
        dataObj.put(ConferenceVariables.CONFERENCE_ID,id);
        dataObj.put(ConferenceVariables.CONFERENCE_NAME,name);
        return new Event(EventType.UPDATE_CONFERENCE,dataObj, Optional.of(id));
    }
}
