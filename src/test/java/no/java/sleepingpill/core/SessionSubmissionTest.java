package no.java.sleepingpill.core;

import no.java.sleepingpill.core.event.Conference;
import no.java.sleepingpill.core.event.ConferenceHolder;
import no.java.sleepingpill.core.servlet.DataServlet;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jsonbuddy.JsonFactory.jsonObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionSubmissionTest {

    private DataServlet dataServlet;

    @Before
    public void cleanAll() throws Exception {
        Method cleanAll = ServiceLocator.class.getDeclaredMethod("cleanAll");
        cleanAll.setAccessible(true);
        cleanAll.invoke(null);
        cleanAll.setAccessible(false);
        dataServlet = new DataServlet();
    }

    @Test
    public void shouldCreateSession() throws Exception {
        String sessionId = addNewSessionGetId();
        assertThat(sessionId).isNotNull();

        JsonObject session = readSession(sessionId);
        assertValueContent(session,"title","My title",false);
    }

    private void assertValueContent(JsonObject session,String key,String expVal,boolean expPrivate) {
        JsonObject titleObject = session.requiredObject(key);
        assertThat(titleObject.requiredString("value")).isEqualTo(expVal);
        assertThat(titleObject.requiredBoolean("privateData")).isEqualTo(expPrivate);
    }

    @Test
    public void shouldCreateAndUpdateSession() throws Exception {
        String sessionId = addNewSessionGetId();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/session/" + sessionId);
        when(request.getMethod()).thenReturn("PUT");

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "Changed title").put("privateData", false))
                        .put("outline", jsonObject().put("value", "Here is my outline").put("privateData", true))
                );
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(input.toJson())));

        dataServlet.service(request,response);

        JsonObject updatedSession = readSession(sessionId);

        assertValueContent(updatedSession,"title","Changed title",false);
        assertValueContent(updatedSession,"outline","Here is my outline",true);

    }

    private JsonObject readSession(String sessionId) throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/session/" + sessionId);
        when(request.getMethod()).thenReturn("GET");
        StringWriter sessionData = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sessionData));

        dataServlet.service(request,response);

        return JsonParser.parseToObject(sessionData.toString());
    }

    private String addNewSessionGetId() throws IOException, ServletException {
        Conference conference = ConferenceHolder.instance().allConferences().get(0);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/event/" + conference.id + "/" + "session");
        when(request.getMethod()).thenReturn("POST");
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(input.toJson())));

        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));

        dataServlet.service(request,response);

        JsonObject result = JsonParser.parseToObject(output.toString());
        return result.requiredString("id");
    }
}
