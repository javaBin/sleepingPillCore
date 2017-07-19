package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.exceptions.InternalError;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.JsonString;
import org.jsonbuddy.pojo.JsonGenerator;
import org.jsonbuddy.pojo.PojoMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DataObject {
    private final String id;
    private final Map<String,DataField> data;

    public DataObject(String id) {
        this.id = id;
        this.data = new ConcurrentHashMap<>();
    }

    public DataObject(DataObject dataObject) {
        this.id = dataObject.id;
        this.data = new ConcurrentHashMap<>(dataObject.data);
    }

    public String getId() {
        return id;
    }

    public Map<String, DataField> getData() {
        Map clone = new HashMap<String, DataField>();
        clone.putAll(data);
        return clone;
    }

    public Optional<DataField> dataValue(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public JsonObject dataAsJson() {
        return (JsonObject) JsonGenerator.generate(data);
    }

    public void addData(JsonObject update) {
        JsonObject values = update.requiredObject("data");
        for (String key : values.keys()) {
            JsonObject value = values.requiredObject(key);
            data.put(key, new DataField(
                    value.value("value").orElseThrow(() -> new InternalError("Missing value in data field")),
                    value.booleanValue("privateData").orElseThrow(() -> new InternalError("Missing privateData in data field"))));
        }

    }

    public Map<String,String> changedPublicFields(DataObject publicVersion) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String,DataField> entry : data.entrySet()) {
            Optional<String> myVal = entry.getValue().readPublicData().filter(jn -> jn instanceof JsonString).map(JsonNode::stringValue);
            if (!myVal.isPresent()) {
                continue;
            }
            Optional<String> pubval = Optional.ofNullable(publicVersion.data.get(entry.getKey()))
                    .map(DataField::readPublicData)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(jn -> jn instanceof JsonString)
                    .map(JsonNode::stringValue);
            if (myVal.equals(pubval)) {
                continue;
            }
            result.put(entry.getKey(),pubval.orElse(""));

        }
        return result;
    }


}
