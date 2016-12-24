package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionVariables;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NewSpeaker implements HasDataInput {
    private Optional<String> id = Optional.empty();
    private Optional<String> name;
    private Optional<String> email;
    private Map<String,DataField> dataFields = new HashMap<>();

    public Optional<String> getId() {
        return id;
    }

    public NewSpeaker setId(Optional<String> id) {
        this.id = id;
        return this;
    }


    public NewSpeaker setName(String name) {
        return setName(Optional.of(name));
    }


    public NewSpeaker setEmail(String email) {
        return setEmail(Optional.of(email));
    }

    public NewSpeaker setName(Optional<String> name) {
        this.name = name;
        return this;
    }

    public NewSpeaker setEmail(Optional<String> email) {
        this.email = email;
        return this;
    }

    public void addData(String key,DataField dataField) {
        dataFields.put(key,dataField);
    }

    public JsonNode asNewEvent() {
        Map<String,DataField> fields = new HashMap<>();
        fields.put("id",DataField.simplePublicStringValue(id.orElse(IdGenerator.newId())));
        if (name.isPresent()) {
            fields.put("name", DataField.simplePublicStringValue(name.get()));
        }
        if (email.isPresent()) {
            fields.put("email", DataField.simplePrivateStringValue(email.get()));
        }

        JsonObject result = (JsonObject) JsonGenerator.generate(fields);
        result.put(SessionVariables.DATA_OBJECT,JsonGenerator.generate(dataFields));

        return result;
    }
}
