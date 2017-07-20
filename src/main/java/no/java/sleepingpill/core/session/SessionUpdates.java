package no.java.sleepingpill.core.session;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SessionUpdates {
    public final Map<String,String> oldValues;
    public final List<SpeakerUpdates> speakerUpdates;

    public SessionUpdates(Map<String, String> oldValues, List<SpeakerUpdates> speakerUpdates) {
        this.oldValues = oldValues;
        this.speakerUpdates = speakerUpdates;
    }

    public static SessionUpdates noUpdates() {
        return new SessionUpdates(Collections.emptyMap(),Collections.emptyList());
    }

    public boolean getHasUnpublishedChanges() {
        return !(oldValues.isEmpty() && speakerUpdates.isEmpty());
    }
}
