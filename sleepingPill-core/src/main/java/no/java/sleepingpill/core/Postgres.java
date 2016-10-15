package no.java.sleepingpill.core;

import no.java.sleepingpill.core.servlet.Configuration;
import org.postgresql.ds.PGPoolingDataSource;

public class Postgres {
    private Postgres() {
    }

    public static PGPoolingDataSource createSource() {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("Postgres Data source");
        source.setServerName(Configuration.dbServer());
        source.setDatabaseName(Configuration.dbName());
        source.setUser(Configuration.dbUser());
        source.setPassword(Configuration.dbPassword());
        source.setMaxConnections(10);
        return source;
    }

}
