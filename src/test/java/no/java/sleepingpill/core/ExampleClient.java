package no.java.sleepingpill.core;

import no.java.sleepingpill.core.util.DataObjects;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public String addNewSession(String conferenceId,Optional<String> httpAddrToAddTo) throws Exception {
        JsonObject speakerObj = JsonFactory.jsonObject()
                .put("name","Darth Vader")
                .put("email","darth@deathstar.com")
                .put("data",JsonFactory.jsonObject().put("bio",JsonFactory.jsonObject().put("value","My bio").put("privateData",false)));
        String titleVal = "My title is blåbærsyltetøy " + LocalDateTime.now().toString();
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", titleVal).put("privateData", false)))
                .put("speakers",JsonFactory.jsonArray().add(speakerObj));

        System.out.println("Posting: " + input);

        String urlArrr = httpAddrToAddTo.orElse(SERVER_ADDRESS + "/conference/" + conferenceId + "/session");
        JsonObject parse = sendDataToServer(input, urlArrr, "POST");
        System.out.println("result from add session:");
        System.out.println(parse);
        return parse.requiredString("id");
    }

    private JsonObject sendDataToServer(JsonObject input, String urlArrr, String methodType) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlArrr).openConnection();
        conn.setRequestMethod(methodType);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("charset", "UTF-8");
        conn.setDoOutput(true);

        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            return JsonParser.parseToObject(is);
        }
    }

    public void updateSession(String sessionId,String speakerid) throws Exception {
        JsonObject luke = JsonFactory.jsonObject()
                .put("name","Luke Skywalker")
                .put("email","luke@deathstar.com")
                .put("data",JsonFactory.jsonObject().put("bio",JsonFactory.jsonObject().put("value","My bio luke").put("privateData",false)));

        JsonObject darthUpdate = JsonFactory.jsonObject()
                .put("id", speakerid)
                .put("data",JsonFactory.jsonObject()
                        .put("postcode", JsonFactory.jsonObject().put("value","1098").put("privateData",true))
                        .put("bio", JsonFactory.jsonObject().put("value","Darth updated bio gosh").put("privateData",false))
                );
        JsonArray speakers = JsonFactory.jsonArray()
                .add(darthUpdate)
                .add(luke);
        JsonObject input = jsonObject()
                .put("data", jsonObject()
                        .put("title", jsonObject().put("value", "Changed title").put("privateData", false))
                        .put("outline", jsonObject().put("value", "Here is my outline").put("privateData", true))
                )
                .put("speakers",speakers);
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
        Optional<String> postNewTo = Optional.ofNullable(args != null && args.length > 0 ? args[0] : null);
        if (postNewTo.isPresent()) {
            exampleClient.addNewSession(null,postNewTo);
            return;
        }
        String conferenceId = exampleClient.addNewConference();
        String newSessionId = exampleClient.addNewSession(conferenceId,postNewTo);
        JsonObject session = exampleClient.readSession(newSessionId);
        String speakerid = session.requiredArray("speakers").get(0,JsonObject.class).requiredString("id");

        exampleClient.updateSession(newSessionId,speakerid);
        session = exampleClient.readSession(newSessionId);
        System.out.println("Current session data:");
        System.out.println(session);
    }


}
