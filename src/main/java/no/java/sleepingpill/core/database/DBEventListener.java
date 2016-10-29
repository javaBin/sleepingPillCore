package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;

import static no.java.sleepingpill.core.database.DBUtil.close;
import static no.java.sleepingpill.core.database.DBUtil.getConnection;

public class DBEventListener implements EventListener {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBEventListener.class);

    boolean active = false;

    @Override
    public void sagaInitialized() {
        active = true;
        logger.info("Event storing activated");
    }

    @Override
    public void eventAdded(Event event) {
        if (!active) {
            return;
        }

        Connection c = null;
        PreparedStatement ps = null;
        String json = event.data.toJson();

        try {
            c = getConnection();
            String sql = "INSERT INTO event ( event_date, event_type, event_submitter, json_data ) " +
                    "VALUES (?, ?, ?, ?)";
            ps = c.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(Clock.systemUTC().instant().toEpochMilli()));
            ps.setString(2, event.eventType.name());
            ps.setString(3, "N/A");
            ps.setString(4, json);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Event insert failed: " + ex.toString());
            throw new RuntimeException(ex);
        } finally {
            close(c, ps, null);
        }
    }

}