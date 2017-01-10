package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionVariables;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpeakerData implements HasDataInput {
    private Optional<String> id = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Map<String,DataField> dataFields = new HashMap<>();

    public Optional<String> getId() {
        return id;
    }

    public SpeakerData setId(Optional<String> id) {
        this.id = id;
        return this;
    }


    public SpeakerData setName(String name) {
        return setName(Optional.of(name));
    }


    public SpeakerData setEmail(String email) {
        return setEmail(Optional.of(email));
    }

    public SpeakerData setName(Optional<String> name) {
        this.name = name;
        return this;
    }

    public SpeakerData setEmail(Optional<String> email) {
        this.email = email;
        return this;
    }

    public SpeakerData addData(String key, DataField dataField) {
        dataFields.put(key,dataField);
        return this;
    }

    public JsonNode eventData() {
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

    public static SpeakerData fromJson(JsonObject jsonObject) {
        SpeakerData speakerData = new SpeakerData();
        speakerData.id =jsonObject.stringValue("id");
        speakerData.name = jsonObject.stringValue(SessionVariables.SPEAKER_NAME);
        speakerData.email = jsonObject.stringValue(SessionVariables.SPEAKER_EMAIL);
        JsonObject dataObject = jsonObject.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject());
        dataObject.keys().forEach(key -> speakerData.addData(key,DataField.fromJson(dataObject.requiredObject(key))));
        return speakerData;
    }
}
