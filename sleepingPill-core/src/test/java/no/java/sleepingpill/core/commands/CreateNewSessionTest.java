package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionHolder;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CreateNewSessionTest {
    @Test
    public void shouldCreateASession() throws Exception {
        EventHandler eventHandler = new EventHandler();
        SessionHolder sessionHolder = new SessionHolder();
        eventHandler.addEventListener(sessionHolder);

        CreateNewSession newSession = new CreateNewSession();
        NewSpeaker newSpeaker = new NewSpeaker();
        newSpeaker.setEmail("darth@deathstar.com");
        newSpeaker.setName("Darth Vader");

        newSession.addSpeaker(newSpeaker);
        newSession.addData("title", DataField.simplePublicStringValue("How to choke"));

        Event event = newSession.createEvent();
        eventHandler.addEvent(event);


        assertThat(sessionHolder.allSessions()).hasSize(1);
    }
}