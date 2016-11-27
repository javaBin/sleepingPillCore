package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.Configuration;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBUtil.class);

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Configuration.dbURL(), Configuration.dbUser(), Configuration.dbPassword()
        );
    }

    public static void close(Connection c, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        try {
            if (ps != null) ps.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        try {
            if (c != null) c.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
    }


    public static void initDB() {

        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = getConnection();
            String sql = "CREATE TABLE event ( " +
                    "   id bigint auto_increment, " +
                    "   event_type varchar(128) not null, " +
                    "   event_date timestamp not null, " +
                    "   event_submitter varchar(128) not null, " +
                    "   json_data text not null " +
                    ")";
            ps = c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Event insert failed: " + ex.toString());
            throw new RuntimeException(ex);
        } finally {
            close(c, ps, null);
        }
    }

    public static boolean dbIsUpToDate() throws SQLException {
        return eventTableExists();
    }

    public static boolean eventTableExists() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            DatabaseMetaData dbm = c.getMetaData();
            ResultSet tables = dbm.getTables(null, null, null, null);
            List<String> l = new ArrayList<>();
            while( tables.next()){
                l.add( tables.getString("TABLE_NAME"));
            }
//            logger.info( "Tables present: " + l);
            return l.stream().map( String::toLowerCase).anyMatch( "event"::equals);
        } finally {
            close(c, ps, null);
        }
    }
}
