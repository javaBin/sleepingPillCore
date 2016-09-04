package no.java.sleepingpill.core;

import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class Talk {
    private final String talkid;
    private final String eventid;
    private final JsonObject privateValues;
    private final JsonObject publicValues;
    private final boolean talkIsPublic;

    public static class Builder {
        private String talkid;
        private String eventid;
        private JsonObject privateValues;
        private JsonObject publicValues;
        private boolean isPublic = false;

        public Talk create() {
            return new Talk(this);
        }

        public Builder setTalkid(String talkid) {
            this.talkid = talkid;
            return this;
        }

        public Builder setEventid(String eventid) {
            this.eventid = eventid;
            return this;
        }

        public Builder setPrivateValues(JsonObject privateValues) {
            this.privateValues = privateValues;
            return this;
        }

        public Builder setPublicValues(JsonObject publicValues) {
            this.publicValues = publicValues;
            return this;
        }

        public Builder setIsPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private Talk(Builder builder) {
        this.talkid = builder.talkid;
        this.eventid = builder.eventid;
        this.privateValues = duplicateObj(builder.privateValues);
        this.publicValues = duplicateObj(builder.publicValues);
        this.talkIsPublic = builder.isPublic;
    }

    private static JsonObject duplicateObj(JsonObject jsonObject) {
        return Optional.ofNullable(jsonObject)
                .orElse(JsonFactory.jsonObject());
    }

    public String getTalkid() {
        return talkid;
    }

    public String getEventid() {
        return eventid;
    }

    public JsonObject getPrivateValues() {
        return privateValues;
    }

    public JsonObject getPublicValues() {
        return publicValues;
    }

    public boolean isPublic() {
        return talkIsPublic;
    }
}
