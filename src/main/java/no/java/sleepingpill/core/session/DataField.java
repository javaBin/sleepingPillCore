package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.JsonString;
import org.jsonbuddy.pojo.OverridesJsonGenerator;

public class DataField implements OverridesJsonGenerator {
    private final JsonNode value;
    private final boolean privateData;

    public DataField(JsonNode value, boolean privateData) {
        this.value = value;
        this.privateData = privateData;
    }

    public static DataField simplePublicStringValue(String value) {
        return new DataField(JsonFactory.jsonString(value),false);
    }

    public static DataField simplePrivateStringValue(String value) {
        return new DataField(JsonFactory.jsonString(value),true);
    }

    public boolean isPrivateData() {
        return privateData;
    }

    public String propertyValue() {
        if (!(value instanceof JsonString)) {
            throw new InternalError("Not a propery value");
        }
        return ((JsonString) value).stringValue();
    }

    @Override
    public JsonObject jsonValue() {
        return JsonFactory.jsonObject()
                .put("value",value.deepClone())
                .put("privateData",privateData);
    }
}
