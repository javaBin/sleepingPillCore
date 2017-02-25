package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.CleanSetupTest;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.ConferenceService;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jsonbuddy.JsonFactory.jsonArray;
import static org.jsonbuddy.JsonFactory.jsonObject;

public class SessionStateServiceTest extends CleanSetupTest {

    private String createASession() {
        JsonObject createConfJson = JsonFactory.jsonObject().put("name", "Javazone 2017").put("slug", "jz17");
        String arrengedEventId = ConferenceService.instance().addConference(createConfJson).getResult().get().requiredString("id");
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
                .put(SessionVariables.VALUE_KEY, dataObject)
                .put(SessionVariables.SPEAKER_ARRAY,speakers);
        ServiceResult serviceResult = SessionService.instance().addSession(arrengedEventId, input);
        return serviceResult.getResult().get().requiredString("id");
    }

    @Test
    public void sessionShouldHaveStatusDraftWhenCreated() throws Exception {
        String sessionid = createASession();
        Session session = SessionHolder.instance().sessionFromId(sessionid).get();
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.DRAFT);
    }

    @Test
    public void shouldUpdateSession() throws Exception {
        String sessionid = createASession();
        ServiceResult serviceResult = SessionService.instance().sessionById(sessionid);
        JsonObject sessionObject = serviceResult.getResult().get();
        String lastUpdated = sessionObject.requiredString(SessionVariables.LAST_UPDATED);

        JsonObject dataObject = jsonObject()
                .put("title", jsonObject().put("value", "New title").put("privateData", false));

        JsonObject input = jsonObject()
                .put(SessionVariables.VALUE_KEY, dataObject)
                .put(SessionVariables.LAST_UPDATED,lastUpdated);


        ServiceResult updateSession = SessionService.instance().updateSession(sessionid, input);
        assertThat(updateSession.getResult()).isPresent();
    }

    @Test
    public void shouldReceiveErrorWhenWrongLastUpdate() throws Exception {
        String sessionid = createASession();
        ServiceResult serviceResult = SessionService.instance().sessionById(sessionid);
        JsonObject sessionObject = serviceResult.getResult().get();
        String lastUpdated = sessionObject.requiredString(SessionVariables.LAST_UPDATED);
        String wrongLastUpdate = lastUpdated + "xx";

        JsonObject dataObject = jsonObject()
                .put("title", jsonObject().put("value", "New title").put("privateData", false));

        JsonObject input = jsonObject()
                .put(SessionVariables.VALUE_KEY, dataObject)
                .put(SessionVariables.LAST_UPDATED,wrongLastUpdate);


        ServiceResult updateSession = SessionService.instance().updateSession(sessionid, input);
        assertThat(updateSession.getResult().isPresent()).isFalse();
        assertThat(updateSession.getError()).isEqualTo(HttpServletResponse.SC_CONFLICT);

    }
}
