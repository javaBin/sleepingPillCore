package no.java.emsreborn;


import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import java.util.Optional;

import static no.java.emsreborn.TalkAttribute.*;
import static org.assertj.core.api.Assertions.assertThat;

public class EventServiceTest extends TestWithTransaction {

    @Test
    public void shouldStoreAndRetriveEvent() throws Exception {
        EventService eventService = EventService.get();
        JsonObject input = JsonFactory.jsonObject().put(EVENT_NAME, "Javazone 2016");
        JsonObject result = eventService.addEvent(input).getResult().get();

        assertThat(result).isNotNull();

        Optional<String> id = result.stringValue(EVENT_ID);
        assertThat(id).isPresent();

        JsonObject event = eventService.findEvent(result).getResult().get();
        assertThat(event.requiredString(EVENT_NAME)).isEqualTo("Javazone 2016");
        assertThat(event.requiredString(EVENT_ID)).isEqualTo(id.get());
    }

    @Test
    public void shouldAddTalk() throws Exception {
        EventService eventService = EventService.get();
        String eventId = eventService.addEvent(JsonFactory.jsonObject().put(EVENT_NAME, "Javazone 2016")).getResult().get().requiredString(EVENT_ID);

        JsonObject newTalk = JsonFactory.jsonObject()
                .put(TALK_PUBLIC_VALUES, JsonFactory.jsonObject().put("name", "MyTalk"));

        JsonObject result = eventService.addTalk(newTalk, eventId).getResult().get();
        String talkid = result.requiredString(TALK_ID);

        assertThat(talkid).isNotNull();

        JsonObject talk = eventService.findTalk(result).getResult().get();
        assertThat(talk.requiredObject(TALK_PUBLIC_VALUES).requiredString("name")).isEqualTo("MyTalk");

    }
}