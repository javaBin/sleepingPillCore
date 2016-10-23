package no.java.sleepingpill.core;

import no.java.sleepingpill.core.commands.CreateNewSession;
import no.java.sleepingpill.core.commands.HasDataInput;
import no.java.sleepingpill.core.commands.NewSpeaker;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.DataField;
import org.jsonbuddy.*;

import java.util.List;
import java.util.Optional;

public class SessionService {
    private static final String DATA_OBJECT = "data";
    private static final String SPEAKER_ARRAY = "speakers";
    private static final String POSTED_BY_MAIL = "postedBy";
    private static final String VALUE_KEY = "value";
    private static final String PRIVATE_FLAG = "privateData";


    public ServiceResult addSession(String arrangedEventId, JsonObject incomingJson) {
        JsonObject talkData = incomingJson.objectValue(DATA_OBJECT).orElse(JsonFactory.jsonObject());
        JsonArray speakers = incomingJson.arrayValue(SPEAKER_ARRAY).orElse(JsonFactory.jsonArray());
        Optional<String> postedBy = incomingJson.stringValue(POSTED_BY_MAIL);

        CreateNewSession createNewSession = new CreateNewSession();
        createNewSession.setArrangedEventId(arrangedEventId);
        createNewSession.setPostedByMail(postedBy);

        speakers.objects(speakobj -> {
            NewSpeaker newSpeaker = new NewSpeaker();
            newSpeaker.setName(speakobj.stringValue("name"));
            newSpeaker.setEmail(speakobj.stringValue("email"));
            addData(speakobj.objectValue(DATA_OBJECT).orElse(JsonFactory.jsonObject()),newSpeaker);
            return newSpeaker;
        }).forEach(createNewSession::addSpeaker);



        addData(talkData, createNewSession);
        Event event = createNewSession.createEvent();
        EventHandler.instance().addEvent(event);

        return ServiceResult.ok(JsonFactory.jsonObject().put("id",createNewSession.getSessionId()));
    }

    private void addData(JsonObject talkData, HasDataInput hasDataInput) {
        for (String key : talkData.keys()) {
            JsonObject valueObject = talkData.requiredObject(key);
            JsonNode jsonValue = valueObject.value(VALUE_KEY).orElse(new JsonNull());
            boolean privateValue = valueObject.booleanValue(PRIVATE_FLAG).orElse(false);
            hasDataInput.addData(key,new DataField(jsonValue,privateValue));
        }
    }
}
