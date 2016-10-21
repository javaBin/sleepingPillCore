package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionHolder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CreateNewSessionTest {

    private EventHandler eventHandler;
    private SessionHolder sessionHolder;

    @Before
    public void setUp() throws Exception {
        eventHandler = new EventHandler();
        sessionHolder = new SessionHolder();
        eventHandler.addEventListener(sessionHolder);
    }

    @Test
    public void shouldCreateASession() throws Exception {
        CreateNewSession newSession = new CreateNewSession();
        NewSpeaker newSpeaker = new NewSpeaker();
        newSpeaker.setEmail("darth@deathstar.com");
        newSpeaker.setName("Darth Vader");

        newSession.addSpeaker(newSpeaker);
        newSession.addData("title", DataField.simplePublicStringValue("How to choke"));

        Event event = newSession.createEvent();
        String sessionId = newSession.getSessionId();
        eventHandler.addEvent(event);

        List<Session> sessions = sessionHolder.allSessions();
        assertThat(sessions).hasSize(1);
        Session session = sessions.get(0);

        assertThat(session.getId()).isEqualTo(sessionId);
        Optional<DataField> title = session.dataValue("title");
        assertThat(title).isPresent();
        assertThat(title.map(DataField::propertyValue)).contains("How to choke");
    }

    @Test
    public void shouldCreateAndUpdateASession() throws Exception {


    }
}