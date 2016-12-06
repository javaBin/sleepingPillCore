package no.java.sleepingpill.core;

import no.java.sleepingpill.core.controller.ConferenceController;
import no.java.sleepingpill.core.controller.ExceptionHandler;
import no.java.sleepingpill.core.controller.SessionController;
import no.java.sleepingpill.core.controller.SubmitterController;
import no.java.sleepingpill.core.database.DBEventReader;
import no.java.sleepingpill.core.database.DBUtil;
import no.java.sleepingpill.core.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static no.java.sleepingpill.core.ServiceLocator.eventHandler;
import static spark.Spark.port;
public class SparkStart {
    private static final Logger logger = LoggerFactory.getLogger(SparkStart.class);
    public static void main(String[] args) {
        setConfigFile(args);
        new SparkStart().start();
        logger.info("Running on port " + Configuration.serverPort());

    }

    private void start() {
        if (Configuration.dbURL() != null) {
            migrateDb();
            loadInitialEvents();
            eventHandler().getEventListeners().forEach(EventListener::sagaInitialized);
        }

        setupAndStartSpark();
    }

    private void setupAndStartSpark() {
        port(Configuration.serverPort());
        new SubmitterController().initSpark();
        new ConferenceController().initSpark();
        new SessionController().initSpark();
        new ExceptionHandler().initSparkExceptionHandling();
    }

    public static void setConfigFile(String[] argv) {
        if (argv != null && argv.length > 0) {
            System.setProperty(Configuration.CONFIG_FILE_PROPERTY, argv[0]);
        }
    }

    private void migrateDb()  {

        try {
            if (!DBUtil.dbIsUpToDate()) {
                DBUtil.initDB();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void loadInitialEvents() {
        new DBEventReader().events().stream().
                forEach(e -> eventHandler().addEvent(e));
    }

}
