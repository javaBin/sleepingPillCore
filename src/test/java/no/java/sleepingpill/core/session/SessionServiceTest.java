package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.CreateNewSession;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class SessionServiceTest {

    public static final String ARRANGED_EVENT_ID="sddsfs234234";
    public static final String POSTED_BY="a@b.com";

    @Before
    public void setup(){
        SessionHolder.instance().clear();
        CreateNewSession createNewSession = new CreateNewSession();
        createNewSession.setArrangedEventId(ARRANGED_EVENT_ID);
        createNewSession.setPostedByMail(Optional.of(POSTED_BY));
        SessionHolder.instance().eventAdded(createNewSession.createEvent());
        SessionHolder.instance().eventAdded(createNewSession.createEvent());
        SessionHolder.instance().eventAdded(createNewSession.createEvent());
    }

    @Test
    public void addSession_state_result() throws Exception {

    }

    @Test
    public void sessionById_state_result() throws Exception {

    }

    @Test
    public void updateSession_state_result() throws Exception {

    }

    @Test
    public void allArrangedEvents_state_result() throws Exception {

    }

    @Test
    public void allSessionsForArrangedEvent_noMatch_ok() throws Exception {
        ServiceResult serviceResult = SessionService.instance().allSessionsForArrangedEvent("");
        assertTrue(serviceResult.getResult().isPresent());
        Optional<JsonArray> optSessions = serviceResult.getResult().get().arrayValue("sessions");
        assertTrue(optSessions.isPresent());
        JsonArray sessions = optSessions.get();
        assertEquals(0, sessions.size());
    }

    @Test
    public void allSessionsForArrangedEvent_null_ok() throws Exception {
        ServiceResult serviceResult = SessionService.instance().allSessionsForArrangedEvent(null);
        assertTrue(serviceResult.getResult().isPresent());
        Optional<JsonArray> optSessions = serviceResult.getResult().get().arrayValue("sessions");
        assertTrue(optSessions.isPresent());
        JsonArray sessions = optSessions.get();
        assertEquals(0, sessions.size());
    }

    @Test
    public void allSessionsForArrangedEvent_match_ok() throws Exception {
        ServiceResult serviceResult = SessionService.instance().allSessionsForArrangedEvent(ARRANGED_EVENT_ID);
        assertTrue(serviceResult.getResult().isPresent());
        Optional<JsonArray> optSessions = serviceResult.getResult().get().arrayValue("sessions");
        assertTrue(optSessions.isPresent());
        JsonArray sessions = optSessions.get();
        assertEquals(3, sessions.size());
    }


}