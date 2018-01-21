package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.CleanSetupTest;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.ConferenceService;
import no.java.sleepingpill.core.conference.ConferenceVariables;
import org.assertj.core.api.Assertions;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConferenceStateServiceTest extends CleanSetupTest {

    @Test
    public void shouldCreateAndUpdateConcference() {
        JsonObject createConfJson = JsonFactory.jsonObject().put(ConferenceVariables.CONFERENCE_NAME, "Jaxazone 2017").put(ConferenceVariables.CONFERENCE_SLUG, "jz17");
        String conferenceid = ConferenceService.instance().addConference(createConfJson).getResult().get().requiredString(ConferenceVariables.CONFERENCE_ID);

        JsonObject updateConferenceJson = JsonFactory.jsonObject().put(ConferenceVariables.CONFERENCE_NAME, "JavaZone 2017");
        ConferenceService.instance().updateConference(conferenceid,updateConferenceJson);

        JsonArray conferences = ConferenceService.instance().allConferences().getResult().get().requiredArray("conferences");
        assertThat(conferences).hasSize(1);
        assertThat(conferences.get(0,JsonObject.class).requiredString(ConferenceVariables.CONFERENCE_NAME)).isEqualTo("JavaZone 2017");

    }
}
