package no.java.emsreborn;

import no.java.emsreborn.dao.EmsDao;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;
import java.util.UUID;

public class EventService {
    public JsonObject addEvent(JsonObject input) {
        String name = input.stringValue("name").get();
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Event event = new Event(emsDao.newKey(), name);
        emsDao.addEvent(event);
        return JsonFactory.jsonObject().put("id",event.eventid);
    }

    public JsonObject findEvent(JsonObject input) {
        String id = input.stringValue("id").get();
        Event event = ServiceLocator.instance().emsDao().findEvent(id).get();

        return JsonFactory.jsonObject()
                .put("id",event.eventid)
                .put("name",event.name);
    }

    public static EventService get() {
        return new EventService();
    }

    public JsonObject addTalk(JsonObject newTalk, String eventId) {
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Talk.Builder builder = Talk.builder()
                .setTalkid(emsDao.newKey())
                .setEventid(eventId);
        Optional<JsonObject> publicValues = newTalk.objectValue("public");
        if (publicValues.isPresent()) {
            builder.setPublicValues(publicValues.get());
        }
        Optional<JsonObject> privateValues = newTalk.objectValue("private");
        if (privateValues.isPresent()) {
            builder.setPrivateValues(privateValues.get());
        }
        Optional<Boolean> isPublic = newTalk.booleanValue("isPublic");
        if (isPublic.isPresent()) {
            builder.setIsPublic(isPublic.get());
        }
        Talk talk = builder.create();
        emsDao.addTalk(talk);

        return JsonFactory.jsonObject().put("id",talk.getTalkid());

    }

    public JsonObject findTalk(JsonObject input) {
        String id = input.stringValue("id").get();
        Talk event = ServiceLocator.instance().emsDao().findTalk(id).get();

        return JsonFactory.jsonObject()
                .put("talkid",event.getTalkid())
                .put("eventid",event.getEventid())
                .put("isPublic",event.isPublic())
                .put("private",event.getPrivateValues())
                .put("public",event.getPublicValues())
                ;
    }
}
