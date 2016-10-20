package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;

public class DataField {
    private final JsonNode value;
    private final boolean privateData;

    public DataField(JsonNode value, boolean privateData) {
        this.value = value;
        this.privateData = privateData;
    }

    public static DataField simplePublicStringValue(String value) {
        return new DataField(JsonFactory.jsonText(value),false);
    }

    public static DataField simplePrivateStringValue(String value) {
        return new DataField(JsonFactory.jsonText(value),true);
    }
}
