package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.commands.CreateNewSession;
import no.java.sleepingpill.core.event.Event;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by narve on 29/10/16.
 */
public class DBEventListenerTest {

    private DBEventListener listener;

    @Test
    public void testEventAdded() throws Exception {

        assertThat("no events at test start", new DBEventReader().events().size(), equalTo(0));

        CreateNewSession creator = new CreateNewSession();
        creator.setConferenceId("asdf");
        Event event = creator.createEvent();
        listener.eventAdded(event);

        assertThat("1 events at test end", new DBEventReader().events().size(), equalTo(1));
    }

    @Before
    public void setupDB() throws SQLException {
        listener = new DBEventListener();
        DBUtil.initDB();
        listener.sagaInitialized();
    }
}