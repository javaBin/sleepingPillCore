package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static no.java.sleepingpill.core.database.DBUtil.close;
import static no.java.sleepingpill.core.database.DBUtil.getConnection;

public class DBEventReader {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBUtil.class);

    public List<Event> events() {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs;

        List<Event> list = new ArrayList<>();
        try {
            c = getConnection();
            String sql = "" +
                    "SELECT " +
                    " id, event_type, event_date, event_submitter, json_data " +
                    "FROM event " +
                    "ORDER BY id";
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(createEvent(rs));
            }
            return list;
        } catch (SQLException ex) {
            logger.error("Event insert failed: " + ex.toString());
            throw new RuntimeException(ex);
        } finally {
            close(c, ps, null);
        }
    }

    private Event createEvent(ResultSet rs) throws SQLException {
        long id = rs.getLong(1);
        String type = rs.getString(2);
        String date = rs.getString(3);
        String submitter = rs.getString(4);
        String json = rs.getString(5);
        Event e = new Event(
                EventType.valueOf(type),
                id,
                JsonParser.parseToObject(json)
        );
        return e;
    }


}
