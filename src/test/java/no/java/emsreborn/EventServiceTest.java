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
    }
}