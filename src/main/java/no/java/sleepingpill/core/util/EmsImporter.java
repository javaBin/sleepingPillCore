package no.java.sleepingpill.core.util;

import no.java.sleepingpill.core.WebServer;
import no.java.sleepingpill.core.servlet.Configuration;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jsonbuddy.JsonFactory.jsonObject;

public class EmsImporter {
    private static final String SUBMIT_LOC = "http://localhost:8082/data/conference/6c599656fdd846468bbcab66cfffbbc0/session";

    public void readEmsData(String arrangedEventUrl) {
        //readFromEms(arrangedEventUrl);
        JsonObject all = readFile("all.json");
        //printToFile(all, "all.json");
        JsonArray allSessions = all.requiredObject("collection").requiredArray("items");
        allSessions.objectStream().forEach(ob -> {
            ob.requiredArray("links").objectStream()
                .filter(liob -> "speaker item".equals(liob.requiredString("rel")))
                .forEach(liob -> {
                    String addr = liob.requiredString("href");
                    String id = addr.substring(addr.lastIndexOf("/") +1);
                    String filename = id + ".json";
                    if (new File(FILEPATH + filename).exists()) {
                        return;
                    }

                    JsonObject jsonObject = readFromEms(addr);
                    printToFile(jsonObject, filename);
                });
        });


    }

    private static class EmsMapping {
        public final String emsname;
        public final String name;
        public final boolean privateData;

        public EmsMapping(String emsname, String name, boolean privateData) {
            this.emsname = emsname;
            this.name = name;
            this.privateData = privateData;
        }
    }

    private static final Map<String,EmsMapping> mapconfig = mapconf();

    private static Map<String, EmsMapping> mapconf() {
        return Stream.of(
                new EmsMapping("format","format",false),
                new EmsMapping("body","abstract",false),
                new EmsMapping("outline","outline",true),
                new EmsMapping("published","published",false),
                new EmsMapping("audience","intendedAudience",false),
                new EmsMapping("slug","slug",false),
                new EmsMapping("equipment","equiplment",true),
                new EmsMapping("title","title",false))
              .collect(Collectors.toMap(m->m.emsname,m->m));
    }

    public void readEmsFromFile(String filnavn) {
        JsonObject all = readFile(filnavn);
        all.requiredObject("collection").requiredArray("items").objectStream()
                .map(item -> item.requiredArray("data"))
                .map(this::dataForSession)
                .forEach(dataobj -> {
                    JsonObject input = jsonObject()
                            .put("data", dataobj);
                    URLConnection conn = openConnection(SUBMIT_LOC, false);
                    conn.setDoOutput(true);
                    try {
                        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
                            input.toJson(printWriter);
                        }
                        try (InputStream is = conn.getInputStream()) {
                            JsonObject parse = JsonParser.parseToObject(is);
                            System.out.println("result from add session:");
                            System.out.println(parse);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public JsonObject dataForSession(JsonArray dataArray) {
        JsonObject result = JsonFactory.jsonObject();
        dataArray.objectStream()
                .filter(ob -> mapconfig.containsKey(ob.requiredString("name")))
                .forEach(ob -> {
                    EmsMapping emsMapping = mapconfig.get(ob.requiredString("name"));
                    JsonObject valobj = JsonFactory.jsonObject()
                            .put("value", ob.requiredString("value"))
                            .put("privateData", emsMapping.privateData);
                    result.put(emsMapping.name,valobj);
                });
        return result;
    }

    private JsonObject readFromEms(String arrangedEventUrl) {
        URLConnection urlConnection = openConnection(arrangedEventUrl, true);
        try (InputStream is = urlConnection.getInputStream()) {
            return JsonParser.parseToObject(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject readFile(String name) {
        try (FileInputStream fileInputStream = new FileInputStream(FILEPATH + name)) {
            return JsonParser.parseToObject(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final String FILEPATH = "/Users/anderskarlsen/Dropbox/javabin/sleepingpilldata/";

    private void printToFile(JsonObject all, String name) {
        try (PrintWriter wr = new PrintWriter(FILEPATH + name, "UTF-8")) {
            all.toJson(wr);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
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
        EmsImporter emsImporter = new EmsImporter();
        //emsImporter.readEmsData("http://javazone.no/ems/server/events/3baa25d3-9cca-459a-90d7-9fc349209289/sessions");
        emsImporter.readEmsFromFile("all.json");
    }

}
