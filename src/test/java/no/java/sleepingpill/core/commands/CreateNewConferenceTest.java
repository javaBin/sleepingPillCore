package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.conference.Conference;
import no.java.sleepingpill.core.conference.ConferenceHolder;
import no.java.sleepingpill.core.conference.ConferenceHolderImpl;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.SessionHolder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateNewConferenceTest {
    private EventHandler eventHandler;
    private ConferenceHolderImpl conferenceHolder;

    @Before
    public void setUp() throws Exception {
        eventHandler = new EventHandler();
        conferenceHolder = new ConferenceHolderImpl();
        eventHandler.addEventListener(conferenceHolder);
    }

    @Test
    public void shouldAddAConference() throws Exception {
        CreateNewConference createNewConference = new CreateNewConference();
        createNewConference.setName("Javazone 2017");
        createNewConference.setSlug("javazone2017");

        Event event = createNewConference.createEvent();
        eventHandler.addEvent(event);

        List<Conference> conferences = conferenceHolder.allConferences();

        assertThat(conferences).hasSize(1);
        assertThat(conferences.get(0).name).isEqualTo("Javazone 2017");

    }
}
