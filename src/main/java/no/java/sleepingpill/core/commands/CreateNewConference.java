package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class CreateNewConference {
    private Optional<String> conferenceId = Optional.empty();
    private String name;
    private String slug;

    public CreateNewConference setConferenceId(String conferenceId) {
        this.conferenceId = Optional.of(conferenceId);
        return this;
    }

    public CreateNewConference setName(String name) {
        this.name = name;
        return this;
    }

    public CreateNewConference setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public Event createEvent() {
        this.conferenceId = Optional.of(conferenceId.orElse(IdGenerator.newId()));
        JsonObject dataObj = JsonFactory.jsonObject();
        dataObj.put("id",conferenceId.get());
        dataObj.put("name",name);
        dataObj.put("slug",slug);
        return new Event(EventType.NEW_CONFERENCE,dataObj);
    }

    public String getId() {
        return conferenceId.orElseThrow(() -> new RuntimeException("Id not set yet"));
    }
}
