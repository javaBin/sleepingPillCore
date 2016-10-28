package no.java.sleepingpill.core;

import no.java.sleepingpill.core.servlet.Configuration;
import no.java.sleepingpill.core.servlet.DataServlet;
import no.java.sleepingpill.core.util.LoggerFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Locale;

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

    private void onlyLogWarningsFromJetty() {
        org.eclipse.jetty.util.log.Log.setLog(LoggerFactory.jettyLogger());
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("org.eclipse.jetty");
        if (!(logger instanceof ch.qos.logback.classic.Logger)) {
            return;
        }
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        logbackLogger.setLevel(ch.qos.logback.classic.Level.WARN);
    }


    protected void start() throws Exception {
        onlyLogWarningsFromJetty();
        //Locale.setDefault(new Locale(Configuration.getLocale()));
        //migrateDb();
        server = new Server(Configuration.serverPort());
        server.setHandler(getHandler());
        server.start();

        LoggerFactory.getLogger(WebServer.class).info("Logger starting. Loglevel: " + Configuration.logLevel());

        System.out.println(server.getURI() + " at " + LocalDateTime.now());
        System.out.println("Path=" + new File(".").getAbsolutePath());
    }

    @SuppressWarnings("unused") // No db yet...
    private void migrateDb() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Postgres.createSource());
        flyway.migrate();
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

    static boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }

}
