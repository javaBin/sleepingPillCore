package no.java.sleepingpill.core;

import no.java.sleepingpill.core.util.DataObjects;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import static org.jsonbuddy.JsonFactory.jsonObject;

public class ExampleClient {
    private static String SERVER_ADDRESS= "http://localhost:8082/data";

    public JsonArray allConferences() throws Exception {
        URL url = new URL(SERVER_ADDRESS + "/conference");
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            JsonObject jsonObject = JsonParser.parseToObject(inputStream);
            return jsonObject.requiredArray("conferences");
        }
    }



    public String addNewConference() throws Exception {
        JsonObject input = DataObjects.newConferenceObj("Javazone 2017","javazone2017",Optional.empty());
        System.out.println("Posting: " + input);

        HttpURLConnection conn = (HttpURLConnection) new URL(SERVER_ADDRESS + "/conference").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonObject parse = JsonParser.parseToObject(is);
            System.out.println("result from add conference:");
            System.out.println(parse);
            return parse.requiredString("id");
        }

    }

    public String addNewSession(String conferenceId) throws Exception {
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));
        System.out.println("Posting: " + input);

        URLConnection conn = new URL(SERVER_ADDRESS + "/conference/" + conferenceId + "/session").openConnection();
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonObject parse = JsonParser.parseToObject(is);
            System.out.println("result from add session:");
            System.out.println(parse);
            return parse.requiredString("id");
        }
    }

    public void updateSession(String sessionId) throws Exception {
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "Changed title").put("privateData", false))
                        .put("outline", jsonObject().put("value", "Here is my outline").put("privateData", true))
                );
        HttpURLConnection conn = (HttpURLConnection) new URL(SERVER_ADDRESS + "/session/" + sessionId).openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonObject parse = JsonParser.parseToObject(is);
            System.out.println("result from update session:");
            System.out.println(parse);
        }

    }

    public JsonObject readSession(String sessionid) throws Exception {
        URL url = new URL(SERVER_ADDRESS + "/session/" + sessionid);
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            JsonObject jsonObject = JsonParser.parseToObject(inputStream);
            return jsonObject;
        }
    }

    public static void main(String[] args) throws Exception {
        ExampleClient exampleClient = new ExampleClient();
        String conferenceId = exampleClient.addNewConference();
        String newSessionId = exampleClient.addNewSession(conferenceId);
        exampleClient.updateSession(newSessionId);
        JsonObject session = exampleClient.readSession(newSessionId);
        System.out.println("Current session data:");
        System.out.println(session);
    }


}
