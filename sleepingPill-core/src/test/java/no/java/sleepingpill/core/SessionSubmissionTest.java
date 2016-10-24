package no.java.sleepingpill.core;

import no.java.sleepingpill.core.event.ArrangedEvent;
import no.java.sleepingpill.core.event.ArrangedEventHolder;
import no.java.sleepingpill.core.servlet.DataServlet;
import org.assertj.core.api.Assertions;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jsonbuddy.JsonFactory.jsonObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionSubmissionTest {
    @Before
    public void cleanAll() throws Exception {
        Method cleanAll = ServiceLocator.class.getDeclaredMethod("cleanAll");
        cleanAll.setAccessible(true);
        cleanAll.invoke(null);
        cleanAll.setAccessible(false);
    }

    @Test
    public void shouldCreateSession() throws Exception {
        ArrangedEvent arrangedEvent = ArrangedEventHolder.instance().allArrangedEvents().get(0);

        DataServlet dataServlet = new DataServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/event/" + arrangedEvent.id + "/" + "session");
        when(request.getMethod()).thenReturn("POST");
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(input.toJson())));
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));

        dataServlet.service(request,response);

        JsonObject result = JsonParser.parseToObject(output.toString());
        String sessionId = result.requiredString("id");
        assertThat(sessionId).isNotNull();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("/session/" + sessionId);
        when(request.getMethod()).thenReturn("GET");
        StringWriter sessionData = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sessionData));

        dataServlet.service(request,response);

        JsonObject session = JsonParser.parseToObject(sessionData.toString());
        JsonObject titleObject = session.requiredObject("title");
        assertThat(titleObject.requiredString("value")).isEqualTo("My title");
        assertThat(titleObject.requiredBoolean("privateData")).isFalse();
    }
}
