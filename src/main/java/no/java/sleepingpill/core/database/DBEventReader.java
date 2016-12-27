package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventType;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DBEventReader {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBEventReader.class);
    private final static String SELECT_SQL = "select id,conferenceid,eventtype,payload from event";

    public List<Event> events() {
        logger.info( "Loading events from db");
        try (
                Connection connection = Postgres.openConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_SQL);
                ResultSet resultSet = statement.executeQuery()) {

            List<Event> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createEvent(resultSet));
            }
            logger.info(String.format("Loaded %d events from db",result.size()));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Event createEvent(ResultSet rs) throws SQLException {
        long id = rs.getLong(1);
        Optional<String> conferenceid = Optional.ofNullable(rs.getString(2));
        EventType eventType = EventType.valueOf(rs.getString(3));
        JsonObject payload = JsonParser.parseToObject(rs.getString(4));
        return new Event(eventType,id,payload,conferenceid);
    }


}
