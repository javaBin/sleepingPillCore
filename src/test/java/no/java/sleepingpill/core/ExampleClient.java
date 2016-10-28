package no.java.sleepingpill.core;

import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExampleClient {
    private static String SERVER_ADDRESS= "http://localhost:8082/data";

    public void showAvailibleArrangedEvents() throws Exception {
        URL url = new URL(SERVER_ADDRESS + "/event");
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            JsonObject jsonObject = JsonParser.parseToObject(inputStream);
            System.out.println(jsonObject);
        }
    }

    public static void main(String[] args) throws Exception {
        new ExampleClient().showAvailibleArrangedEvents();
    }
}
