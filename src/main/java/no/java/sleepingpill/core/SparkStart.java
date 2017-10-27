package no.java.sleepingpill.core;

import no.java.sleepingpill.core.controller.*;
import no.java.sleepingpill.core.database.DBEventReader;
import no.java.sleepingpill.core.database.Postgres;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventListener;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static no.java.sleepingpill.core.ServiceLocator.eventHandler;
import static spark.Spark.after;
import static spark.Spark.port;
public class SparkStart {
    private static final Logger logger = LoggerFactory.getLogger(SparkStart.class);
    public static void main(String[] args) {
        setConfigFile(args);
        new SparkStart().start();
        logger.info("Running on port " + Configuration.serverPort());

    }

    private void start() {
        if (Configuration.persistToDb()) {
            logger.info("Setting up db");
            migrateDb();
            loadInitialEvents();
            eventHandler().getEventListeners().forEach(EventListener::sagaInitialized);
        } else {
            logger.warn("Running without persistance. Data will be lost when server shuts down");
        }

        setupAndStartSpark();
    }

    private void setupAndStartSpark() {
        port(Configuration.serverPort());
        new SubmitterController().initSpark();
        new ConferenceController().initSpark();
        new SessionController().initSpark();
        new PictureController().initSpark();
        new PublicDataController().initSpark();

        new BasicAuthController().initSpark();
        new ExceptionHandler().initSparkExceptionHandling();
        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });
    }

    public static void setConfigFile(String[] argv) {
        if (argv != null && argv.length > 0) {
            System.setProperty(Configuration.CONFIG_FILE_PROPERTY, argv[0]);
        }
    }

    private void migrateDb()  {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Postgres.source());
        if (Configuration.cleanDb()) {
            logger.warn("Cleaning db");
            flyway.clean();
        }
        flyway.migrate();
    }

    void loadInitialEvents() {
        List<Event> events = new DBEventReader().events();
        events.forEach(e -> eventHandler().addEvent(e));
    }

}
