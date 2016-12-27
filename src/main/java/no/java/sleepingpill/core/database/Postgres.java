package no.java.sleepingpill.core.database;

import no.java.sleepingpill.core.Configuration;
import org.postgresql.ds.PGPoolingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Postgres {
    private Postgres() {
    }

    private static volatile PGPoolingDataSource source;

    public static PGPoolingDataSource source() {
        if (source != null) {
            return source;
        }
        return createSource();
    }

    private synchronized static PGPoolingDataSource createSource() {
        if (source != null) {
            return source;
        }
        source = new PGPoolingDataSource();
        source.setDataSourceName("Postgres Data source");
        source.setServerName(Configuration.dbServer());
        source.setDatabaseName(Configuration.dbName());
        source.setUser(Configuration.dbUser());
        source.setPassword(Configuration.dbPassword());
        source.setPortNumber(Configuration.dbPort());
        source.setMaxConnections(Configuration.maxDbConnections());
        return source;
    }

    public static Connection openConnection() {
        try {
            return source().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
