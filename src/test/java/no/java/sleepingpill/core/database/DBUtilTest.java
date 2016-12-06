package no.java.sleepingpill.core.database;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static no.java.sleepingpill.core.database.DBUtil.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by narve on 29/10/16.
 */
@Ignore("No db at the moment")
public class DBUtilTest {

    @Before
    public void setupDB() throws SQLException {

        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = getConnection();
            String sql = "DROP TABLE event";
            ps = c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
        } finally {
            close(c, ps, null);
        }
    }

    @Test
    public void testEventTableExists() throws Exception {
        assertThat("table does not exist at test start", eventTableExists(), equalTo(false));
        assertThat("db is not update to date at test start", dbIsUpToDate(), equalTo(false));
        DBUtil.initDB();
        assertThat("table  exists at test end", eventTableExists(), equalTo(true));
        assertThat("db is update to date at test end", dbIsUpToDate(), equalTo(true));
    }
}