package no.java.sleepingpill.core;

import no.java.sleepingpill.core.database.DBEventReader;
import no.java.sleepingpill.core.database.DBUtil;
import no.java.sleepingpill.core.event.EventListener;
import no.java.sleepingpill.core.servlet.Configuration;
import no.java.sleepingpill.core.servlet.DataServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static no.java.sleepingpill.core.ServiceLocator.eventHandler;

public class WebServer {
    private static Server server;

    public static void main(String[] argv) throws Exception {
        setConfigFile(argv);
        new WebServer().start();
    }

    public static void setConfigFile(String[] argv) {
        if (argv != null && argv.length > 0) {
            System.setProperty(Configuration.CONFIG_FILE_PROPERTY, argv[0]);
        }
    }

    static boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }

    protected void start() throws Exception {
        //Locale.setDefault(new Locale(Configuration.getLocale()));
        migrateDb();
        loadInitialEvents();
        eventHandler().getEventListeners().stream()
                .forEach(EventListener::sagaInitialized);

        server = new Server(Configuration.serverPort());
        server.setHandler(getHandler());

        server.start();

        System.out.println(server.getURI() + " at " + LocalDateTime.now());
        System.out.println("Path=" + new File(".").getAbsolutePath());
    }

    void migrateDb() throws SQLException {
        if (!DBUtil.dbIsUpToDate()) {
            DBUtil.initDB();
        }
    }

    void loadInitialEvents() {
        new DBEventReader().events().stream().
                forEach(e -> eventHandler().addEvent(e));
    }

    protected WebAppContext getHandler() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(30);
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            // Development ie running in ide
            System.out.println("Running from ide");
            webAppContext.setResourceBase("src/main/resources/webapp");
        } else {
            // Prod ie running from jar
            System.out.println("Running from jar");
            webAppContext.setBaseResource(Resource.newClassPathResource("webapp", true, false));
        }


        webAppContext.addServlet(new ServletHolder(new DataServlet()), "/data/*");
        return webAppContext;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void stop() throws Exception {
        server.stop();
    }

}
