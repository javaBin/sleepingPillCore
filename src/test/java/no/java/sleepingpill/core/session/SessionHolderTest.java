package no.java.sleepingpill.core.session;


import no.java.sleepingpill.core.commands.CreateNewSession;
import no.java.sleepingpill.core.commands.SpeakerData;
import no.java.sleepingpill.core.commands.UpdateSession;
import no.java.sleepingpill.core.event.Event;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SessionHolderTest {
    private static final String CONFERENCE_ID = "eventx";

    private SessionHolder sessionHolder = new SessionHolder();

    @Test
    public void shouldFindSessionByEmail() throws Exception {
        SpeakerData darth = new SpeakerData()
            .setEmail("darth@deathstar.com")
            .setName("Darth Vader")
            .addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        CreateNewSession sessionOne = new CreateNewSession()
                .setConferenceId(CONFERENCE_ID)
                .addSpeaker(darth)
                .addData("title", DataField.simplePublicStringValue("SessionOne"));

        sessionHolder.eventAdded(sessionOne.createEvent());

        List<Session> sessions = sessionHolder.sessionsByEmail("darth@deathstar.com");
        assertThat(sessions).hasSize(1).extracting(Session::getId).containsOnly(sessionOne.getSessionId());

    }

    @Test
    public void shouldBeAbleToUpdateBio() throws Exception {
        SpeakerData darth = new SpeakerData()
                .setEmail("darth@deathstar.com")
                .setName("Darth Vader")
                .addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        CreateNewSession sessionOne = new CreateNewSession()
                .setConferenceId(CONFERENCE_ID)
                .addSpeaker(darth)
                .addData("title", DataField.simplePublicStringValue("SessionOne"));

        sessionHolder.eventAdded(sessionOne.createEvent());

        Session session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        Speaker darthSpeaker = session.getSpeakers().get(0);

        SpeakerData darthUpdate = new SpeakerData()
                .setId(Optional.of(darthSpeaker.getId()))
                .addData("bio", DataField.simplePublicStringValue("Darth updated bio"));

        UpdateSession updateSession = new UpdateSession(session.getId(),CONFERENCE_ID)
                .addSpeakerData(darthUpdate);

        Event addSpeakerEvent = updateSession.createEvent();
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(1);
        darthSpeaker = session.getSpeakers().get(0);

        assertThat(darthSpeaker.getEmail()).isEqualTo("darth@deathstar.com");
        assertThat(darthSpeaker.dataValue("bio")).contains(DataField.simplePublicStringValue("Darth updated bio"));


    }

    @Test
    public void shouldBeAbleToAddASpeaker() throws Exception {
        SpeakerData darth = new SpeakerData()
                .setEmail("darth@deathstar.com")
                .setName("Darth Vader")
                .addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        CreateNewSession sessionOne = new CreateNewSession()
                .setConferenceId(CONFERENCE_ID)
                .addSpeaker(darth)
                .addData("title", DataField.simplePublicStringValue("SessionOne"));

        sessionHolder.eventAdded(sessionOne.createEvent());

        Session session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        Speaker darthSpeaker = session.getSpeakers().get(0);

        SpeakerData darthUpdate = new SpeakerData()
                .setId(Optional.of(darthSpeaker.getId()));

        SpeakerData luke = new SpeakerData()
                .setEmail("luke@endor.com")
                .setName("Luke Skywalker")
                .addData("bio",DataField.simplePublicStringValue("Is Darth Vader my father?"));


        UpdateSession updateSession = new UpdateSession(session.getId(),CONFERENCE_ID)
                .addSpeakerData(darthUpdate)
                .addSpeakerData(luke);

        Event addSpeakerEvent = updateSession.createEvent();
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(2);

    }


    @Test
    public void shouldBeAbleToDeleteSpeaker() throws Exception {
        SpeakerData darth = new SpeakerData()
                .setEmail("darth@deathstar.com")
                .setName("Darth Vader")
                .addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        SpeakerData luke = new SpeakerData()
                .setEmail("luke@endor.com")
                .setName("Luke Skywalker")
                .addData("bio",DataField.simplePublicStringValue("Is Darth Vader my father?"));


        CreateNewSession sessionOne = new CreateNewSession()
                .setConferenceId(CONFERENCE_ID)
                .addSpeaker(darth)
                .addSpeaker(luke)
                .addData("title", DataField.simplePublicStringValue("SessionOne"));

        sessionHolder.eventAdded(sessionOne.createEvent());

        Session session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        Speaker darthSpeaker = session.getSpeakers().get(0);

        SpeakerData darthUpdate = new SpeakerData()
                .setId(Optional.of(darthSpeaker.getId()));



        UpdateSession updateSession = new UpdateSession(session.getId(),CONFERENCE_ID)
                .addSpeakerData(darthUpdate);

        Event addSpeakerEvent = updateSession.createEvent();
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionOne.getSessionId()).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(1);
        assertThat(session.getSpeakers().get(0).getId()).isEqualTo(darthSpeaker.getId());

    }

}