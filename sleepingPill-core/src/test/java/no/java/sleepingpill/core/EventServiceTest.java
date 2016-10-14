package no.java.sleepingpill.core;


import no.java.sleepingpill.core.domain.TalkAttribute;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EventServiceTest extends TestWithTransaction {

    @Test
    public void shouldStoreAndRetriveEvent() throws Exception {
        EventService eventService = EventService.get();
        JsonObject input = JsonFactory.jsonObject().put(TalkAttribute.EVENT_NAME, "Javazone 2016");
        JsonObject result = eventService.addEvent(input).getResult().get();

        assertThat(result).isNotNull();

        Optional<String> id = result.stringValue(TalkAttribute.EVENT_ID);
        assertThat(id).isPresent();

        JsonObject event = eventService.findEvent(result).getResult().get();
        assertThat(event.requiredString(TalkAttribute.EVENT_NAME)).isEqualTo("Javazone 2016");
        assertThat(event.requiredString(TalkAttribute.EVENT_ID)).isEqualTo(id.get());
    }

    @Test
    public void shouldAddTalk() throws Exception {
        EventService eventService = EventService.get();
        String eventId = eventService.addEvent(JsonFactory.jsonObject().put(TalkAttribute.EVENT_NAME, "Javazone 2016")).getResult().get().requiredString(TalkAttribute.EVENT_ID);

        JsonObject newTalk = JsonFactory.jsonObject()
                .put(TalkAttribute.TALK_PUBLIC_VALUES, JsonFactory.jsonObject().put("name", "MyTalk"));

        JsonObject result = eventService.addTalk(newTalk, eventId).getResult().get();
        String talkid = result.requiredString(TalkAttribute.TALK_ID);

        assertThat(talkid).isNotNull();

        JsonObject talk = eventService.findTalk(result).getResult().get();
        assertThat(talk.requiredObject(TalkAttribute.TALK_PUBLIC_VALUES).requiredString("name")).isEqualTo("MyTalk");

    }
}