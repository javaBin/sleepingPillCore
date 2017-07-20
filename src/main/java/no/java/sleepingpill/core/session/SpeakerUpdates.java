package no.java.sleepingpill.core.session;

import org.jsonbuddy.JsonObject;

import java.util.List;
import java.util.Map;

public class SpeakerUpdates {
    public final String speakerid;
    public final List<JsonObject> publicValues;
    public final UpdateType updateType;

    public SpeakerUpdates(String speakerid, List<JsonObject> publicValues, UpdateType updateType) {
        this.speakerid = speakerid;
        this.publicValues = publicValues;
        this.updateType = updateType;
    }
}
