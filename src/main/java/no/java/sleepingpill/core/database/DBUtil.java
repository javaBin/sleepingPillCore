package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.servlet.Configuration;
import org.slf4j.Logger;

import java.sql.*;

public class DBUtil {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DBUtil.class);

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Configuration.dbURL(), Configuration.dbUser(), Configuration.dbPassword()
        );
    }

    public static void close(Connection c, PreparedStatement ps, ResultSet rs) {
        try {
            c.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        try {
            ps.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        try {
            rs.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
    }

}
