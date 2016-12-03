package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonObject;

public class Speaker extends DataObject {
    private final String sessionId;
    private final String name;
    private final String email;

    private Speaker(String sessionid, String id, JsonObject input) {
        super(id);
        this.sessionId = sessionid;
        this.name = input.requiredObject("name").requiredString("value");
        this.email = input.requiredObject("email").requiredString("value");

    }


    public static Speaker fromJson(String sessionid,JsonObject input) {
        String id = input.requiredObject("id").requiredString("value");
        Speaker speaker = new Speaker(sessionid, id, input);
        speaker.addData(input);
        return speaker;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
