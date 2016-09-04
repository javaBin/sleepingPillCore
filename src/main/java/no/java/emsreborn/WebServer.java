package no.java.emsreborn;

import no.java.emsreborn.servlet.Configuration;
import no.java.emsreborn.servlet.DataServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

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
        Locale.setDefault(new Locale("no"));
        if (argv != null && argv.length > 0) {
//            System.setProperty(Configuration.CONFIG_FILE_PROPERTY, argv[0]);
        }
    }


    protected void start() throws Exception {
        //Locale.setDefault(new Locale(Configuration.getLocale()));
        //migrateDb();
        server = new Server(Configuration.serverPort());
        server.setHandler(getHandler());
        server.start();

        System.out.println(server.getURI() + " at " + LocalDateTime.now());
        System.out.println("Path=" + new File(".").getAbsolutePath());
    }



    protected WebAppContext getHandler() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(30);
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            // Development ie running in ide
            webAppContext.setResourceBase("src/main/resources/webapp");
        } else {
            // Prod ie running from jar
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
