package no.java.sleepingpill.core.session;


import no.java.sleepingpill.core.commands.CreateNewSession;
import no.java.sleepingpill.core.commands.NewSpeaker;
import no.java.sleepingpill.core.event.Event;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionHolderTest {
    private SessionHolder sessionHolder = new SessionHolder();
    @Test
    public void shouldFindSessionByEmail() throws Exception {
        NewSpeaker darth = new NewSpeaker()
            .setEmail("darth@deathstar.com")
            .setName("Darth Vader")
            .addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        CreateNewSession sessionOne = new CreateNewSession().setConferenceId("eventx");

        sessionOne.addSpeaker(darth);
        sessionOne.addData("title", DataField.simplePublicStringValue("SessionOne"));



        sessionHolder.eventAdded(sessionOne.createEvent());

        List<Session> sessions = sessionHolder.sessionsByEmail("darth@deathstar.com");
        assertThat(sessions).hasSize(1).extracting(Session::getId).containsOnly(sessionOne.getSessionId());

    }
}