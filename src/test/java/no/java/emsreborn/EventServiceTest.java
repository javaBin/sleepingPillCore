package no.java.emsreborn;


import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EventServiceTest extends TestWithTransaction {

    @Test
    public void shouldStoreAndRetriveEvent() throws Exception {
        EventService eventService = EventService.get();
        JsonObject input = JsonFactory.jsonObject().put("name", "Javazone 2016");
        JsonObject result = eventService.addEvent(input);

        assertThat(result).isNotNull();

        Optional<String> id = result.stringValue("id");
        assertThat(id).isPresent();

        JsonObject event = eventService.findEvent(result);
        assertThat(event.requiredString("name")).isEqualTo("Javazone 2016");
        assertThat(event.requiredString("id")).isEqualTo(id.get());
    }

    @Test
    public void shouldAddTalk() throws Exception {
        EventService eventService = EventService.get();
        String eventId = eventService.addEvent(JsonFactory.jsonObject().put("name", "Javazone 2016")).requiredString("id");

        JsonObject newTalk = JsonFactory.jsonObject()
                .put("public", JsonFactory.jsonObject().put("name", "MyTalk"));

        JsonObject result = eventService.addTalk(newTalk, eventId);
        String talkid = result.requiredString("id");

        assertThat(talkid).isNotNull();

        JsonObject talk = eventService.findTalk(result);
        assertThat(talk.requiredObject("public").requiredString("name")).isEqualTo("MyTalk");

    }
}