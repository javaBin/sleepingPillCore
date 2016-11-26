package no.java.sleepingpill.core.conference;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.CreateNewConference;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import java.util.List;
import java.util.Optional;

public class ConferenceService {
    private static ConferenceService instance = new ConferenceService();

    public static ConferenceService instance() {
        return instance;
    }

    public ServiceResult addConference(JsonObject incomingData) {
        CreateNewConference createNewConference = new CreateNewConference()
                .setName(incomingData.requiredString("name"))
                .setSlug(incomingData.requiredString("slug"));
        Optional<String> conferenceid = incomingData.stringValue("id");
        if (conferenceid.isPresent()) {
            createNewConference.setConferenceId(conferenceid.get());
        }

        Event event = createNewConference.createEvent();
        EventHandler.instance().addEvent(event);
        JsonObject result = JsonFactory.jsonObject().put("id", createNewConference.getId());
        return ServiceResult.ok(result);

    }

    public ServiceResult allConferences() {
        List<Conference> conferences = ServiceLocator.conferenceHolder().allConferences();
        JsonObject result = JsonFactory.jsonObject().put("conferences", JsonGenerator.generate(conferences));
        return ServiceResult.ok(result);
    }

}
