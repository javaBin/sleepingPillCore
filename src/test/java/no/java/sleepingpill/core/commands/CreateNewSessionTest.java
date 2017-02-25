package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        CreateNewSession newSession = new CreateNewSession().setConferenceId("eventx");
        SpeakerData speakerData = new SpeakerData();
        speakerData.setEmail("darth@deathstar.com");
        speakerData.setName("Darth Vader");
        speakerData.addData("bio",DataField.simplePublicStringValue("Here is my bio"));

        newSession.addSpeaker(speakerData);
        newSession.addData("title", DataField.simplePublicStringValue("How to choke"));

        Event event = newSession.createEvent();
        String sessionId = newSession.getSessionId();
        eventHandler.addEvent(event);

        List<Session> sessions = sessionHolder.allSessions();
        assertThat(sessions).hasSize(1);
        Session session = sessions.get(0);

        assertThat(session.getId()).isEqualTo(sessionId);
        validateValue(session, "title", "How to choke");
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.DRAFT);

        List<Speaker> speakers = session.getSpeakers();
        assertThat(speakers).hasSize(1);
        Speaker speaker = speakers.get(0);

        assertThat(speaker.getId()).isNotNull();
        assertThat(speaker.getName()).isEqualTo("Darth Vader");




    }

    @Test
    public void shouldCreateAndUpdateASession() throws Exception {
        CreateNewSession newSession = new CreateNewSession().setConferenceId("eventx");
        SpeakerData speakerData = new SpeakerData();
        speakerData.setEmail("darth@deathstar.com");
        speakerData.setName("Darth Vader");

        newSession.addSpeaker(speakerData);
        newSession.addData("title", DataField.simplePublicStringValue("How to choke"));
        newSession.addData("description", DataField.simplePublicStringValue("Initial description"));

        Event event = newSession.createEvent();
        String sessionId = newSession.getSessionId();
        eventHandler.addEvent(event);

        UpdateSession updateSession = new UpdateSession(sessionId, "eventx");
        updateSession.addData("description", DataField.simplePublicStringValue("Updated description"));
        updateSession.addData("audience", DataField.simplePublicStringValue("Do not need one"));
        updateSession.setSessionStatus(SessionStatus.SUBMITTED);

        Event updateSessionEvent = updateSession.createEvent(sessionHolder.sessionFromId(sessionId).get());
        eventHandler.addEvent(updateSessionEvent);

        List<Session> sessions = sessionHolder.allSessions();
        assertThat(sessions).hasSize(1);
        Session session = sessions.get(0);

        assertThat(session.getId()).isEqualTo(sessionId);
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.SUBMITTED);
        validateValue(session, "title", "How to choke");
        validateValue(session, "description", "Updated description");
        validateValue(session, "audience", "Do not need one");

    }

    @Test
    public void shouldCreateHistoricSession() throws Exception {
        CreateNewSession newSession = new CreateNewSession().setConferenceId("eventx");
        SpeakerData speakerData = new SpeakerData();
        speakerData.setEmail("darth@deathstar.com");
        speakerData.setName("Darth Vader");

        newSession.addSpeaker(speakerData);
        newSession.addData("title", DataField.simplePublicStringValue("How to choke"));
        newSession.addData("description", DataField.simplePublicStringValue("Initial description"));
        newSession.setSessionStatus(Optional.of(SessionStatus.HISTORIC));

        Event event = newSession.createEvent();
        String sessionId = newSession.getSessionId();
        eventHandler.addEvent(event);

        List<Session> sessions = sessionHolder.allSessions();
        assertThat(sessions).hasSize(1);
        Session session = sessions.get(0);

        assertThat(session.getId()).isEqualTo(sessionId);
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.HISTORIC);

    }

    private void validateValue(Session session, String key, String expectedValue) {
        Optional<DataField> title = session.dataValue(key);
        assertThat(title).isPresent();
        assertThat(title.map(DataField::propertyValue)).contains(expectedValue);
    }
}