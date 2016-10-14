package no.java.sleepingpill.core.domain;

import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.OverridesJsonGenerator;

import java.util.Optional;

public class Talk {
    public final boolean isPublic;
    public final JsonObject publicData;
    public final JsonObject privateData;
    public final Optional<JsonObject> inReview;

    private Talk(JsonObject talkJson) {
        this.isPublic = talkJson.requiredBoolean(TalkAttribute.TALK_IS_PUBLIC);
        this.publicData = talkJson.requiredObject(TalkAttribute.TALK_PUBLIC_VALUES);
        this.privateData = talkJson.requiredObject(TalkAttribute.TALK_PRIVATE_VALUES);
        this.inReview = talkJson.objectValue(TalkAttribute.TALK_IN_REVIEW);
    }

    public static Talk fromJson(JsonObject jsonObject) {
        return new Talk(jsonObject);
    }

    public JsonObject toJson() {
        return JsonFactory.jsonObject()
                .put(TalkAttribute.TALK_IS_PUBLIC,isPublic);
    }
}
