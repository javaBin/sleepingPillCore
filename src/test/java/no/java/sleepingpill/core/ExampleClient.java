package no.java.sleepingpill.core;

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

import static org.jsonbuddy.JsonFactory.jsonObject;

public class ExampleClient {
    private static String SERVER_ADDRESS= "http://localhost:8082/data";

    public JsonArray allArrangedEvents() throws Exception {
        URL url = new URL(SERVER_ADDRESS + "/event");
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            JsonObject jsonObject = JsonParser.parseToObject(inputStream);
            return jsonObject.requiredArray("arrangedEvents");
        }
    }

    public String addNewSession(String arrangedEventId) throws Exception {
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));
        System.out.println("Posting: " + input);

        URLConnection conn = new URL(SERVER_ADDRESS + "/event/" + arrangedEventId + "/session").openConnection();
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

    public static void main(String[] args) throws Exception {
        ExampleClient exampleClient = new ExampleClient();
        String arrangedEventId = exampleClient.allArrangedEvents().get(0, JsonObject.class).requiredString("id");
        String newSessionId = exampleClient.addNewSession(arrangedEventId);
        exampleClient.updateSession(newSessionId);

    }


}