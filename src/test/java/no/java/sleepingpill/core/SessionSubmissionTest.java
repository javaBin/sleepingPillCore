package no.java.sleepingpill.core;

import no.java.sleepingpill.core.controller.ConferenceController;
import no.java.sleepingpill.core.controller.SessionController;
import no.java.sleepingpill.core.session.SessionVariables;
import org.jsonbuddy.JsonObject;
import org.junit.Test;
import spark.Request;
import spark.Response;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jsonbuddy.JsonFactory.jsonObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionSubmissionTest extends CleanSetupTest {

    @Test
    public void shouldCreateConference() throws Exception {
        String conferenceid = createNewConferenceGetId();
        assertThat(conferenceid).isNotNull();
    }



    @Test
    public void shouldCreateSession() throws Exception {
        String conferenceid = createNewConferenceGetId();
        String sessionId = addNewSessionGetId(conferenceid);
        assertThat(sessionId).isNotNull();

        JsonObject session = readSession(sessionId);
        assertValueContent(session,"title","My title",false);
    }

    private void assertValueContent(JsonObject session,String key,String expVal,boolean expPrivate) {
        JsonObject dataObj = session.requiredObject(SessionVariables.DATA_OBJECT);
        JsonObject titleObject = dataObj.requiredObject(key);
        assertThat(titleObject.requiredString("value")).isEqualTo(expVal);
        assertThat(titleObject.requiredBoolean("privateData")).isEqualTo(expPrivate);
    }

    @Test
    public void shouldCreateAndUpdateSession() throws Exception {
        String conferenceid = createNewConferenceGetId();
        String sessionId = addNewSessionGetId(conferenceid);

        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "Changed title").put("privateData", false))
                        .put("outline", jsonObject().put("value", "Here is my outline").put("privateData", true))
                );

        Request req = mock(Request.class);
        Response resp = mock(Response.class);

        when(req.params(":id")).thenReturn(sessionId);
        when(req.body()).thenReturn(input.toJson());

        SessionController sessionController = new SessionController();
        sessionController.putUpdateSession(req, resp);

        JsonObject updatedSession = readSession(sessionId);

        assertValueContent(updatedSession,"title","Changed title",false);
        assertValueContent(updatedSession,"outline","Here is my outline",true);

    }

    private JsonObject readSession(String sessionId) throws IOException, ServletException {
        Request req = mock(Request.class);
        Response resp = mock(Response.class);
        when(req.params(":id")).thenReturn(sessionId);

        SessionController sessionController = new SessionController();
        ServiceResult serviceResult = sessionController.getSessionById(req, resp);


        return serviceResult.getResult().get();
    }

    private String createNewConferenceGetId() {
        Request req = mock(Request.class);
        Response resp = mock(Response.class);

        JsonObject input = jsonObject()
                .put("name", "Javazone 2017")
                .put("slug", "javazone2017")
                ;
        when(req.body()).thenReturn(input.toJson());
        ConferenceController conferenceController = new ConferenceController();

        ServiceResult serviceResult = conferenceController.postAddConference(req, resp);

        String id = serviceResult.getResult().get().requiredString("id");

        return id;
    }

    private String addNewSessionGetId(String conferenceid) throws IOException, ServletException {
        SessionController sessionController = new SessionController();

        Request req = mock(Request.class);
        Response resp = mock(Response.class);

        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));

        when(req.params(":conferenceId")).thenReturn(conferenceid);
        when(req.body()).thenReturn(input.toJson());

        ServiceResult serviceResult = sessionController.postAddSession(req, resp);

        JsonObject result = serviceResult.getResult().get();
        return result.requiredString("id");
    }
}
