package no.java.emsreborn;

import no.java.emsreborn.dao.EmsDao;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

import static no.java.emsreborn.TalkAttribute.*;

public class EventService {
    public ServiceResult addEvent(JsonObject input) {
        String name = input.stringValue(EVENT_NAME).get();
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Event event = new Event(emsDao.newKey(), name);
        emsDao.addEvent(event);
        return ServiceResult.ok(JsonFactory.jsonObject().put(EVENT_ID,event.eventid));
    }

    public ServiceResult findEvent(JsonObject input) {
        String id = input.stringValue(EVENT_ID).get();
        Event event = ServiceLocator.instance().emsDao().findEvent(id).get();

        return ServiceResult.ok(JsonFactory.jsonObject()
                .put(EVENT_ID,event.eventid)
                .put(EVENT_NAME,event.name));
    }

    public static EventService get() {
        return new EventService();
    }

    public ServiceResult addTalk(JsonObject newTalk, String eventId) {
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Talk.Builder builder = Talk.builder()
                .setTalkid(emsDao.newKey())
                .setEventid(eventId);
        Optional<JsonObject> publicValues = newTalk.objectValue(TALK_PUBLIC_VALUES);
        if (publicValues.isPresent()) {
            builder.setPublicValues(publicValues.get());
        }
        Optional<JsonObject> privateValues = newTalk.objectValue(TALK_PRIVATE_VALUES);
        if (privateValues.isPresent()) {
            builder.setPrivateValues(privateValues.get());
        }
        Optional<Boolean> isPublic = newTalk.booleanValue(TALK_PRIVATE_VALUES);
        if (isPublic.isPresent()) {
            builder.setIsPublic(isPublic.get());
        }
        Talk talk = builder.create();
        emsDao.addTalk(talk);

        return ServiceResult.ok(JsonFactory.jsonObject().put(TALK_ID,talk.getTalkid()));

    }

    public ServiceResult findTalk(JsonObject input) {
        String id = input.stringValue(TALK_ID).get();
        Talk event = ServiceLocator.instance().emsDao().findTalk(id).get();

        return ServiceResult.ok(JsonFactory.jsonObject()
                .put(TALK_ID,event.getTalkid())
                .put(EVENT_ID,event.getEventid())
                .put(TALK_IS_PUBLIC,event.isPublic())
                .put(TALK_PRIVATE_VALUES,event.getPrivateValues())
                .put(TALK_PUBLIC_VALUES,event.getPublicValues())
        );
    }
}
