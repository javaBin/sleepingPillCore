package no.java.sleepingpill.core;

import no.java.sleepingpill.core.dao.EmsDao;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class EventService {
    public ServiceResult addEvent(JsonObject input) {
        String name = input.stringValue(TalkAttribute.EVENT_NAME).get();
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Event event = new Event(emsDao.newKey(), name);
        emsDao.addEvent(event);
        return ServiceResult.ok(JsonFactory.jsonObject().put(TalkAttribute.EVENT_ID,event.eventid));
    }

    public ServiceResult findEvent(JsonObject input) {
        String id = input.stringValue(TalkAttribute.EVENT_ID).get();
        Event event = ServiceLocator.instance().emsDao().findEvent(id).get();

        return ServiceResult.ok(JsonFactory.jsonObject()
                .put(TalkAttribute.EVENT_ID,event.eventid)
                .put(TalkAttribute.EVENT_NAME,event.name));
    }

    public static EventService get() {
        return new EventService();
    }

    public ServiceResult addTalk(JsonObject newTalk, String eventId) {
        EmsDao emsDao = ServiceLocator.instance().emsDao();
        Talk.Builder builder = Talk.builder()
                .setTalkid(emsDao.newKey())
                .setEventid(eventId);
        Optional<JsonObject> publicValues = newTalk.objectValue(TalkAttribute.TALK_PUBLIC_VALUES);
        if (publicValues.isPresent()) {
            builder.setPublicValues(publicValues.get());
        }
        Optional<JsonObject> privateValues = newTalk.objectValue(TalkAttribute.TALK_PRIVATE_VALUES);
        if (privateValues.isPresent()) {
            builder.setPrivateValues(privateValues.get());
        }
        Optional<Boolean> isPublic = newTalk.booleanValue(TalkAttribute.TALK_PRIVATE_VALUES);
        if (isPublic.isPresent()) {
            builder.setIsPublic(isPublic.get());
        }
        Talk talk = builder.create();
        emsDao.addTalk(talk);

        return ServiceResult.ok(JsonFactory.jsonObject().put(TalkAttribute.TALK_ID,talk.getTalkid()));

    }

    public ServiceResult findTalk(JsonObject input) {
        String id = input.stringValue(TalkAttribute.TALK_ID).get();
        Talk event = ServiceLocator.instance().emsDao().findTalk(id).get();

        return ServiceResult.ok(JsonFactory.jsonObject()
                .put(TalkAttribute.TALK_ID,event.getTalkid())
                .put(TalkAttribute.EVENT_ID,event.getEventid())
                .put(TalkAttribute.TALK_IS_PUBLIC,event.isPublic())
                .put(TalkAttribute.TALK_PRIVATE_VALUES,event.getPrivateValues())
                .put(TalkAttribute.TALK_PUBLIC_VALUES,event.getPublicValues())
        );
    }
}
