package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.Configuration;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.Map;
import java.util.Optional;

public class Speaker extends DataObject {
    private final String sessionId;
    private final String name;
    private final String email;

    private Speaker(String sessionid, String id, JsonObject input) {
        super(id);
        this.sessionId = sessionid;
        this.name = input.requiredObject(SessionVariables.SPEAKER_NAME).requiredString("value");
        this.email = input.requiredObject(SessionVariables.SPEAKER_EMAIL).requiredString("value");

    }

    public Speaker(Speaker speaker) {
        super(speaker);
        this.sessionId = speaker.sessionId;
        this.name = speaker.name;
        this.email = speaker.email;
    }



    public static Speaker fromJson(String sessionid,JsonObject input) {
        String id = input.requiredObject("id").requiredString("value");
        Speaker speaker = new Speaker(sessionid, id, input);
        speaker.addData(input);
        return speaker;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public JsonObject singleSessionData() {
        JsonObject json = JsonFactory.jsonObject()
                .put("name", name)
                .put("email", email)
                .put("id", getId())
                .put("data", super.dataAsJson());
        return json;
    }

    public Speaker update(JsonObject updateJson) {

        JsonObject input = JsonFactory.jsonObject();
        input.put(SessionVariables.SPEAKER_NAME,
                DataField.simplePublicStringValue(updateJson.objectValue(SessionVariables.SPEAKER_NAME)
                    .map(ob -> ob.requiredString("value"))
                    .orElse(this.name)).jsonValue());
        input.put(SessionVariables.SPEAKER_EMAIL,
                DataField.simplePrivateStringValue(updateJson.objectValue(SessionVariables.SPEAKER_EMAIL)
                        .map(ob -> ob.requiredString("value"))
                        .orElse(this.email)).jsonValue());
        JsonObject data = (JsonObject) JsonGenerator.generate(getData());
        JsonObject dataUpdate = updateJson.objectValue(SessionVariables.DATA_OBJECT).orElse(JsonFactory.jsonObject());
        for (String key : dataUpdate.keys()) {
            data.put(key,dataUpdate.value(key).get());
        }
        input.put(SessionVariables.DATA_OBJECT,data);

        Speaker newSpeaker = new Speaker(this.sessionId, this.getId(), input);
        newSpeaker.addData(input);
        return newSpeaker;
    }

    public JsonObject asPublicJson() {
        JsonObject result = JsonFactory.jsonObject();
        result.put(SessionVariables.SPEAKER_NAME,name);

        Map<String, DataField> data = getData();
        for (String key : data.keySet()) {
            data.get(key).readPublicData().ifPresent(da -> result.put(key,da));
        }
        addPictureUrl(data,result);
        return result;
    }

    private void addPictureUrl(Map<String, DataField> data, JsonObject result) {
        Optional<String> pictureIdOpt = Optional.ofNullable(data.get("pictureId")).map(DataField::propertyValue);
        if (!pictureIdOpt.isPresent()) {
            return;
        }
        String url = Configuration.serverAddress() + "/public/picture/" + pictureIdOpt.get();
        result.put("pictureUrl",url);
    }

    public Map<String, String> changedPublicFields(Speaker publicVersion) {
        Map<String, String> changedPublicFields = super.changedPublicFields(publicVersion);
        if (!name.equals(publicVersion.name)) {
            changedPublicFields.put("name",publicVersion.name);
        }
        return changedPublicFields;
    }
}
