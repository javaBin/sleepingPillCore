package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DBEventListener implements EventListener {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBEventListener.class);

    boolean sagaIsInitalized = false;

    @Override
    public void sagaInitialized() {
        sagaIsInitalized = true;
        logger.info("Event storing activated");
    }

    private final static String INSERT_SQL = "insert into event(id,conferenceid,eventtype,payload) values (?,?,?,?)";

    @Override
    public void eventAdded(Event event) {
        if (!sagaIsInitalized) {
            return;
        }
        try (Connection connection = Postgres.openConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setLong(1,event.index);
            statement.setString(2,event.conferenceId.orElse(null));
            statement.setString(3,event.eventType.toString());
            statement.setString(4,event.data.toJson());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}