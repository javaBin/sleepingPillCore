package no.java.sleepingpill.core.session;

import java.util.HashMap;
import java.util.Map;

public class DataObject {
    private final String id;
    private Map<String,DataField> data = new HashMap<>();

    public DataObject(String id) {
        this.id = id;
    }
}
