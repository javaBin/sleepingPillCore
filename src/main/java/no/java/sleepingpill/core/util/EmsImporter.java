package no.java.sleepingpill.core.util;

import no.java.sleepingpill.core.WebServer;
import no.java.sleepingpill.core.servlet.Configuration;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.parse.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class EmsImporter {
    public void importEmsForYear(String arrangedEventUrl) {
        URLConnection urlConnection = openConnection(arrangedEventUrl, true);
        try (InputStream is = urlConnection.getInputStream()) {
            JsonNode parser = JsonParser.parse(is);
            System.out.println(parser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URLConnection openConnection(String questionUrl, boolean useAuthorization) {
        try {
            URL url = new URL(questionUrl);
            URLConnection urlConnection = url.openConnection();

            if (useAuthorization) {
                String authString = Configuration.emmsUser() + ":" + Configuration.emsPassword();
                String authStringEnc = Base64Util.encode(authString);
                urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            }

            return urlConnection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Supply configfile");
            return;
        }
        WebServer.setConfigFile(args);
        new EmsImporter().importEmsForYear("http://javazone.no/ems/server/events/3baa25d3-9cca-459a-90d7-9fc349209289/sessions");
    }

}
