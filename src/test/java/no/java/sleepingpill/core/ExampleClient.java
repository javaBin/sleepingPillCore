package no.java.sleepingpill.core;

import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    public String addNewSession() throws Exception {
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "My title").put("privateData", false)));
        System.out.println("Posting: " + input);
        String arrangedEventId = allArrangedEvents().get(0, JsonObject.class).requiredString("id");
        URLConnection conn = new URL(SERVER_ADDRESS + "/event/" + arrangedEventId + "/session").openConnection();
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonNode parse = JsonParser.parse(is);
            System.out.println("result:");
            System.out.println(parse);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ExampleClient exampleClient = new ExampleClient();
        //JsonArray result = exampleClient.allArrangedEvents();
        exampleClient.addNewSession();
    }


}
