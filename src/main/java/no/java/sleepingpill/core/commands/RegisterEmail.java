package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class RegisterEmail {
    private String email;
    private Optional<String> id = Optional.empty();

    public RegisterEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id.orElseThrow(() -> new InternalError("Email Id not set yet"));
    }

    public Event createEvent() {
        this.id = Optional.of(this.id.orElse(IdGenerator.newId()));
        JsonObject data = JsonFactory.jsonObject()
                .put("email", email)
                .put("id", id.get());
        return new Event(EventType.EMAIL_CONFIRMED, data);
    }
}
