package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;
import org.jsonbuddy.pojo.OverridesJsonGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NewSpeaker {
    private Optional<String> id = Optional.empty();
    private String name;
    private String email;
    private Map<String,DataField> dataFields = new HashMap<>();

    public Optional<String> getId() {
        return id;
    }

    public NewSpeaker setId(Optional<String> id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public NewSpeaker setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public NewSpeaker setEmail(String email) {
        this.email = email;
        return this;
    }

    public Map<String, DataField> getDataFields() {
        return dataFields;
    }

    public JsonNode asNewEvent() {
        Map<String,DataField> fields = new HashMap<>();
        fields.putAll(dataFields);
        fields.put("id",DataField.simplePublicStringValue(id.orElse(IdGenerator.newId())));
        fields.put("name",DataField.simplePublicStringValue(name));
        fields.put("email",DataField.simplePublicStringValue(name));

        return JsonGenerator.generate(fields);
    }
}
