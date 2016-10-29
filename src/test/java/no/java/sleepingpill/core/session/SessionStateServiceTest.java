package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.CleanSetupTest;
import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.event.Conference;
import org.assertj.core.api.Assertions;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jsonbuddy.JsonFactory.jsonArray;
import static org.jsonbuddy.JsonFactory.jsonObject;

public class SessionStateServiceTest extends CleanSetupTest {

    private String createASession() {
        List<Conference> arrangedEvents = ServiceLocator.conferenceHolder().allConferences();
        String arrengedEventId = arrangedEvents.get(0).id;
        JsonObject dataObject = jsonObject()
                .put("title", jsonObject().put("value", "My title").put("privateData", false))
                .put("abstract", jsonObject().put("value", "This is the abstract").put("privateData", false))
                .put("outline", jsonObject().put("value", "Here is my outline").put("privateData", true))
                .put("expectedAudience", jsonObject().put("value", "Here is my outline").put("privateData", false));
        JsonArray speakers = jsonArray().add(jsonObject()
                .put("name", "Darth Vader")
                .put("email", "darth@deathstar.com")
                .put("data", jsonObject().put("bio", jsonObject().put("value", "I was Any before").put("privateData", false)))
        );
        JsonObject input = jsonObject()
                .put(SessionService.VALUE_KEY, dataObject)
                .put(SessionService.SPEAKER_ARRAY,speakers);
        ServiceResult serviceResult = SessionService.instance().addSession(arrengedEventId, input);
        return serviceResult.getResult().get().requiredString("id");
    }

    @Test
    public void sessionShouldHaveStatusDraftWhenCreated() throws Exception {
        String sessionid = createASession();
        Session session = SessionHolder.instance().sessionFromId(sessionid).get();
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.DRAFT);
    }
}
