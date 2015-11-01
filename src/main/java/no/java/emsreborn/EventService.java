package no.java.emsreborn;

import no.java.emsreborn.dao.EmsDao;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

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
}
