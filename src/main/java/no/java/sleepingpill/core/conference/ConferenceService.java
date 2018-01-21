package no.java.sleepingpill.core.conference;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.CreateNewConference;
import no.java.sleepingpill.core.commands.UpdateConference;
import no.java.sleepingpill.core.commands.UpdateSession;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.pojo.JsonGenerator;

import javax.servlet.http.HttpServletResponse;
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

    public ServiceResult updateConference(String id, JsonObject payload) {
        Optional<String> name = payload.stringValue(ConferenceVariables.CONFERENCE_NAME);
        if (!name.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required value " +ConferenceVariables.CONFERENCE_NAME);

        }
        Optional<Conference> conference = ServiceLocator.conferenceHolder().allConferences().stream().filter(conf -> conf.id.equals(id)).findAny();
        if (!conference.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown conferenceid " + id);
        }
        UpdateConference updateConference = new UpdateConference(id,name.get());
        Event event = updateConference.createEvent();
        EventHandler.instance().addEvent(event);
        return ServiceResult.ok(JsonFactory.jsonObject());
    }
}
