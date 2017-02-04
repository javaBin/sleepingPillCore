package no.java.sleepingpill.core.session;

import org.jsonbuddy.*;
import org.jsonbuddy.pojo.OverridesJsonGenerator;

import java.util.Objects;
import java.util.Optional;

public class DataField implements OverridesJsonGenerator {
    private final JsonNode value;
    private final boolean privateData;

    public DataField(JsonNode value, boolean privateData) {
        this.value = value;
        this.privateData = privateData;
    }

    public static DataField fromJson(JsonObject jsonObject) {
        JsonNode value = jsonObject.value("value").orElseThrow(() -> new JsonValueNotPresentException("DataField missing key value"));
        boolean privateData = jsonObject.requiredBoolean("privateData");
        return new DataField(value, privateData);
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

    private JsonNode getValue() {
        return value.deepClone();
    }

    @Override
    public JsonObject jsonValue() {
        return JsonFactory.jsonObject()
                .put("value",value.deepClone())
                .put("privateData",privateData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataField)) return false;
        DataField dataField = (DataField) o;
        return privateData == dataField.privateData &&
                Objects.equals(value, dataField.value);
    }

    public Optional<JsonNode> readPublicData() {
        if (isPrivateData()) {
            return Optional.empty();
        }
        return Optional.of(getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, privateData);
    }

    @Override
    public String toString() {
        return "DataField{" +
                "value=" + value +
                ", privateData=" + privateData +
                '}';
    }
}
