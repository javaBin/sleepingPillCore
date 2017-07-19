package no.java.sleepingpill.core.session;

import java.util.Map;

public class SpeakerUpdates {
    public final String speakerid;
    public final Map<String,String> publicValues;
    public final UpdateType updateType;

    public SpeakerUpdates(String speakerid, Map<String, String> publicValues, UpdateType updateType) {
        this.speakerid = speakerid;
        this.publicValues = publicValues;
        this.updateType = updateType;
    }
}
