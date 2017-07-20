package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SessionUpdates {
    public final List<JsonObject> oldValues;
    public final List<SpeakerUpdates> speakerUpdates;

    public SessionUpdates(List<JsonObject> oldValues, List<SpeakerUpdates> speakerUpdates) {
        this.oldValues = oldValues;
        this.speakerUpdates = speakerUpdates;
    }

    public static SessionUpdates noUpdates() {
        return new SessionUpdates(Collections.emptyList(),Collections.emptyList());
    }

    public boolean getHasUnpublishedChanges() {
        return !(oldValues.isEmpty() && speakerUpdates.isEmpty());
    }
}
