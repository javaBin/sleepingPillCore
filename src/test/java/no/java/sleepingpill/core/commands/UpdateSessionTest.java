package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.exceptions.SessionChangedException;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionHolder;
import no.java.sleepingpill.core.session.SessionVariables;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UpdateSessionTest {
    private static final String CONFERENCE_ID = "eventx";
    private EventHandler eventHandler;
    private SessionHolder sessionHolder;
    private String sessionid;

    @Before
    public void setUp() throws Exception {
        eventHandler = new EventHandler();
        sessionHolder = new SessionHolder();
        eventHandler.addEventListener(sessionHolder);

        CreateNewSession newSession = new CreateNewSession().setConferenceId(CONFERENCE_ID);
        newSession.addData("title", DataField.simplePublicStringValue("titleone"));
        eventHandler.addEvent(newSession.createEvent());
        sessionid = newSession.getSessionId();
    }

    @Test
    public void shouldBeAbleToUpdateSessionWithoutLastUpdated() throws Exception {
        Session session = getSessionFromHolder();
        String origupdated = session.getLastUpdated();
        assertThat(origupdated).isNotNull();

        UpdateSession updateSession = new UpdateSession(sessionid,CONFERENCE_ID);
        DataField updatedTitle = DataField.simplePublicStringValue("titletwo");
        updateSession.addData("title", updatedTitle);
        eventHandler.addEvent(updateSession.createEvent(session));

        session = getSessionFromHolder();

        assertThat(session.getData().get("title")).isEqualTo(updatedTitle);
        assertThat(session.getLastUpdated()).isNotNull().isNotEqualTo(origupdated);
    }

    @Test
    public void shouldFailUpdateWhenSupplingDifferentLastUpdate() throws Exception {
        Session session = getSessionFromHolder();
        String origupdated = session.getLastUpdated();
        UpdateSession updateSession = new UpdateSession(sessionid,CONFERENCE_ID);
        DataField updatedTitle = DataField.simplePublicStringValue("titletwo");
        updateSession.addData("title", updatedTitle);
        String actualLastChange = origupdated + "xxx";
        updateSession.setLastUpdated(actualLastChange);

        try {
            updateSession.createEvent(session);
            fail("Expected SessionChangedException");
        } catch (SessionChangedException ex) {
            assertThat(ex.actualLastChange).isEqualTo(actualLastChange);
            assertThat(ex.correctLastChange).isEqualTo(origupdated);
        }
    }

    private Session getSessionFromHolder() {
        Optional<Session> sessionOptional = sessionHolder.sessionFromId(sessionid);
        assertThat(sessionOptional).isPresent();
        return sessionOptional.get();
    }
}
