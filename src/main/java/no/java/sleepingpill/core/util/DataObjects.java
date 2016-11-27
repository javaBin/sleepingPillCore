package no.java.sleepingpill.core.util;

import org.jsonbuddy.JsonObject;

import java.util.Optional;

import static org.jsonbuddy.JsonFactory.jsonObject;

public class DataObjects {
    public static JsonObject newConferenceObj(String name, String slug, Optional<String> id) {
        JsonObject input = jsonObject()
                .put("name", name)
                .put("slug", slug)
                ;
        id.ifPresent(s -> input.put("id", s));
        return input;
    }
}
