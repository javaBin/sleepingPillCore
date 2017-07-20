package no.java.sleepingpill.core.session;


import no.java.sleepingpill.core.commands.*;
import no.java.sleepingpill.core.event.Event;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SessionHolderTest {
    private static final String CONFERENCE_ID = "eventx";

    private SessionHolder sessionHolder = new SessionHolder();

    @Test
    public void shouldFindSessionByEmail() throws Exception {
        String sessionId = createSession();

        List<Session> sessions = sessionHolder.sessionsByEmail("darth@deathstar.com");
        assertThat(sessions).hasSize(1).extracting(Session::getId).containsOnly(sessionId);

    }

    private String createSession() {
        SpeakerData darth = new SpeakerData()
            .setEmail("darth@deathstar.com")
            .setName("Darth Vader")
            .addData("bio", DataField.simplePublicStringValue("Here is my bio"));

        CreateNewSession sessionOne = new CreateNewSession()
                .setConferenceId(CONFERENCE_ID)
                .addSpeaker(darth)
                .addData("title", DataField.simplePublicStringValue("SessionOne"));

        sessionHolder.eventAdded(sessionOne.createEvent());
        return sessionOne.getSessionId();
    }

    @Test
    public void shouldBeAbleToUpdateBio() throws Exception {
        String sessionId = createSession();

        Session session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        Speaker darthSpeaker = session.getSpeakers().get(0);

        SpeakerData darthUpdate = new SpeakerData()
                .setId(Optional.of(darthSpeaker.getId()))
                .addData("bio", DataField.simplePublicStringValue("Darth updated bio"));

        UpdateSession updateSession = new UpdateSession(session.getId(),CONFERENCE_ID)
                .addSpeakerData(darthUpdate);

        Event addSpeakerEvent = updateSession.createEvent(session);
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(1);
        darthSpeaker = session.getSpeakers().get(0);

        assertThat(darthSpeaker.getEmail()).isEqualTo("darth@deathstar.com");
        assertThat(darthSpeaker.dataValue("bio")).contains(DataField.simplePublicStringValue("Darth updated bio"));


    }

    @Test
    public void shouldBeAbleToAddASpeaker() throws Exception {
        String sessionId = createSession();
        Session session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        UpdateSession publisSessionEvent=new UpdateSession(sessionId,CONFERENCE_ID).setSessionStatus(SessionStatus.APPROVED);
        sessionHolder.eventAdded(publisSessionEvent.createEvent(session));

        session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

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

        Event addSpeakerEvent = updateSession.createEvent(session);
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(2);
        assertThat(session.asPublicSessionJson().requiredArray(SessionVariables.SPEAKER_ARRAY)).hasSize(1);
        assertThat(session.getSessionUpdates().getHasUnpublishedChanges()).isTrue();
        List<SpeakerUpdates> speakerUpdates = session.getSessionUpdates().speakerUpdates;

        assertThat(speakerUpdates).hasSize(1);
        SpeakerUpdates addedSpeaker = speakerUpdates.get(0);
        assertThat(addedSpeaker.updateType).isEqualTo(UpdateType.ADDED);
        assertThat(addedSpeaker.speakerid).isNotNull().isNotEqualTo(darthSpeaker.getId());

    }


    @Test
    public void shouldBeAbleToDeleteSpeaker() throws Exception {
        String sessionId = createSession();

        Session session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));


        Speaker darthSpeaker = session.getSpeakers().get(0);

        SpeakerData darthUpdate = new SpeakerData()
                .setId(Optional.of(darthSpeaker.getId()));



        UpdateSession updateSession = new UpdateSession(session.getId(),CONFERENCE_ID)
                .addSpeakerData(darthUpdate);

        Event addSpeakerEvent = updateSession.createEvent(session);
        sessionHolder.eventAdded(addSpeakerEvent);

        session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSpeakers()).hasSize(1);
        assertThat(session.getSpeakers().get(0).getId()).isEqualTo(darthSpeaker.getId());

    }

    @Test
    public void shouldDeleteSession() throws Exception {
        String sessionId = createSession();

        DeleteSession deleteSession = new DeleteSession(CONFERENCE_ID,sessionId);

        sessionHolder.eventAdded(deleteSession.createEvent());

        assertThat(sessionHolder.allSessions()).isEmpty();

    }

    @Test
    public void shouldBeAbleToAddComment() throws Exception {
        String sessionId = createSession();

        Session session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        UpdateSession updateSession = new UpdateSession(session.getId(), CONFERENCE_ID);
        updateSession.setLastUpdated(session.getLastUpdated());

        List<NewComment> newComments = Collections.singletonList(
                new NewComment("anders@pkom", "Program comitee", "Please fill in all fields in the form"));

        updateSession.addComments(newComments);

        Event addCommentEvent = updateSession.createEvent(session);
        sessionHolder.eventAdded(addCommentEvent);

        session = sessionHolder.sessionFromId(sessionId).orElseThrow(() -> new RuntimeException("Did not find session"));

        List<Comment> sessionComments = session.getComments();

        assertThat(sessionComments).hasSize(1);

        Comment comment = sessionComments.get(0);

        assertThat(comment.getId()).isNotNull();

    }

    @Test
    public void shouldMarkUpdatedTitle() throws Exception {
        String sessionid = createSession();
        Session session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.DRAFT);
        UpdateSession publisSessionEvent=new UpdateSession(sessionid,CONFERENCE_ID).setSessionStatus(SessionStatus.APPROVED);
        sessionHolder.eventAdded(publisSessionEvent.createEvent(session));

        session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.APPROVED);

        UpdateSession updateTitle=new UpdateSession(sessionid,CONFERENCE_ID).addData("title",DataField.simplePublicStringValue("Updated title"));
        sessionHolder.eventAdded(updateTitle.createEvent(session));

        session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));

        Optional<DataField> title = session.dataValue("title");
        assertThat(title.get().propertyValue()).isEqualTo("Updated title");
        assertThat(session.asPublicSessionJson().requiredString("title")).isEqualTo("SessionOne");

        SessionUpdates sessionUpdates = session.getSessionUpdates();
        assertThat(sessionUpdates.oldValues.keySet()).hasSize(1);
        assertThat(sessionUpdates.oldValues.get("title")).isEqualTo("SessionOne");


    }

    @Test
    public void shouldBeAbleToPublishChanges() throws Exception {
        String sessionid = createSession();
        Session session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));
        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.DRAFT);
        UpdateSession publisSessionEvent=new UpdateSession(sessionid,CONFERENCE_ID).setSessionStatus(SessionStatus.APPROVED);
        sessionHolder.eventAdded(publisSessionEvent.createEvent(session));

        session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));

        assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.APPROVED);

        UpdateSession updateTitle=new UpdateSession(sessionid,CONFERENCE_ID).addData("title",DataField.simplePublicStringValue("Updated title"));
        sessionHolder.eventAdded(updateTitle.createEvent(session));


        sessionHolder.eventAdded(new PublishChangesToSession(sessionid,CONFERENCE_ID).createEvent());

        session = sessionHolder.sessionFromId(sessionid).orElseThrow(() -> new RuntimeException("Did not find session"));

        Optional<DataField> title = session.dataValue("title");
        assertThat(title.get().propertyValue()).isEqualTo("Updated title");
        assertThat(session.asPublicSessionJson().requiredString("title")).isEqualTo("Updated title");

        SessionUpdates sessionUpdates = session.getSessionUpdates();
        assertThat(sessionUpdates.getHasUnpublishedChanges()).isFalse();


    }
}