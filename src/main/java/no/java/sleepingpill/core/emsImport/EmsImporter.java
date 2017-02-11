package no.java.sleepingpill.core.emsImport;

import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionStatus;
import no.java.sleepingpill.core.session.SessionVariables;
import no.java.sleepingpill.core.util.Base64Util;
import no.java.sleepingpill.core.util.DataObjects;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jsonbuddy.JsonFactory.jsonObject;

public class EmsImporter {
    private static final String SUBMIT_LOC = "http://localhost:8082/data/conference/6c599656fdd846468bbcab66cfffbbc0/session";

    public EmsImporter(String filepath) {
        FILEPATH = filepath;
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
                new EmsMapping("equipment","equipment",true),
                new EmsMapping("title","title",false),
                new EmsMapping("keywords","keywords",false),
                new EmsMapping("tags","tags",true),
                new EmsMapping("lang","language",false)

                )
              .collect(Collectors.toMap(m->m.emsname,m->m));
    }



    public JsonObject dataForSession(JsonObject sessionObject) {
        JsonArray dataArray = sessionObject.requiredArray("data");
        JsonObject dataObject = JsonFactory.jsonObject();
        dataArray.objectStream()
                .filter(ob -> mapconfig.containsKey(ob.requiredString("name")))
                .forEach(emsDataObject -> {
                    addValueFromEms(dataObject, emsDataObject);
                });

        dataObject.put("emslocation",JsonFactory.jsonObject().put(SessionVariables.PRIVATE_FLAG,true).put(SessionVariables.VALUE_KEY,sessionObject.requiredString("href")));

        SessionStatus sessionStatus = Optional.of("true").equals(itemValue(dataArray,"published")) &&
        Optional.of("approved").equals(itemValue(dataArray,"state"))
        ? SessionStatus.HISTORIC : SessionStatus.REJECTED;
        String speakerUrl = sessionObject.requiredArray("links").objectStream()
                .filter(ob -> ob.requiredString("rel").equals("speaker collection"))
                .map(ob -> ob.requiredString("href"))
                .findAny().get();
        JsonObject emsSpeaker = readFromEms(speakerUrl, true);
        JsonArray speakerNode = emsSpeaker.objectValue("collection").orElse(JsonFactory.jsonObject()).arrayValue("items").orElse(JsonFactory.jsonArray());
        JsonArray speakers = JsonArray.fromNodeStream(speakerNode.objectStream().map(speakerObj -> {
            JsonArray dataValues = speakerObj.requiredArray("data");
            String name = itemValue(dataValues, "name").orElseThrow(() -> new RuntimeException("Could not find speaker name"));
            String email = itemValue(dataValues, "email").orElseThrow(() -> new RuntimeException("Could not find speaker email"));
            Optional<String> zip = itemValue(dataValues, "zip-code").filter(s -> !s.isEmpty());
            String bio = itemValue(dataValues, "bio").orElse("Speaker bio not present");

            JsonObject dataobj = JsonFactory.jsonObject()
                    .put("bio", DataField.simplePublicStringValue(bio).jsonValue());
            zip.ifPresent(s -> dataobj.put("zip-code", DataField.simplePrivateStringValue(s).jsonValue()));
            JsonObject speakerJson = JsonFactory.jsonObject()
                    .put("name", name)
                    .put("email", email)
                    .put("data", dataobj);

            return speakerJson;
        }));

        return JsonFactory.jsonObject()
                .put(SessionVariables.DATA_OBJECT,dataObject)
                .put(SessionVariables.SPEAKER_ARRAY,speakers)
                .put(SessionVariables.SESSION_STATUS,sessionStatus.toString());
    }

    private void addValueFromEms(JsonObject dataObject, JsonObject emsDataObject) {
        EmsMapping emsMapping = mapconfig.get(emsDataObject.requiredString("name"));
        JsonObject valobj = JsonFactory.jsonObject()
                .put("privateData", emsMapping.privateData);

        Optional<String> stringval = emsDataObject.stringValue("value");
        if (stringval.isPresent()) {
            valobj.put("value", stringval.get());
            dataObject.put(emsMapping.name,valobj);
            return;
        }
        Optional<JsonArray> arrayValue = emsDataObject.arrayValue("array");
        if (arrayValue.isPresent()) {
            valobj.put("value",arrayValue.get());
            dataObject.put(emsMapping.name,valobj);
        }
    }

    private static Optional<String> itemValue(JsonArray items,String key) {
        return items.objectStream()
                .filter(ob -> Optional.of(key).equals(ob.stringValue("name")) && ob.stringValue("value").isPresent())
                .map(ob -> ob.requiredString("value"))
                .findAny();
    }

    private JsonObject readFromEms(String emsUrl, boolean useAuthorization) {
        URLConnection urlConnection = openConnection(emsUrl,useAuthorization);
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
    private final String FILEPATH;

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
                String authString = EmsImportConfig.emsUser() + ":" + EmsImportConfig.emsPassword();
                String authStringEnc = Base64Util.encode(authString);
                urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            }

            return urlConnection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmsConference> readAndCreateConferences() {
        JsonObject readFromEms = readFromEms(EmsImportConfig.emsAddress(), false);
        JsonArray emsConferences = readFromEms.requiredObject("collection").requiredArray("items");
        List<JsonObject> matchingConferences = emsConferences.objectStream()
                .filter(this::matchesSelectedConference)
                .collect(Collectors.toList());
        List<EmsConference> result = new ArrayList<>();

        for (JsonObject conferenceObj : matchingConferences) {
            String href = conferenceObj.requiredString("href");
            String id = href.substring(href.lastIndexOf("/") +1 );



            JsonArray dataArray = conferenceObj.requiredArray("data");
            String name = dataArray.objectStream().filter(ob -> ob.requiredString("name").equals("name")).map(ob -> ob.requiredString("value")).findAny().get();
            String slug = dataArray.objectStream().filter(ob -> ob.requiredString("name").equals("slug")).map(ob -> ob.requiredString("value")).findAny().get();

            JsonObject payload = DataObjects.newConferenceObj(name, slug, Optional.of(id));

            postData(EmsImportConfig.serverAddress() + "/conference",payload);

            String sessionCollection = conferenceObj.requiredArray("links").objectStream()
                    .filter(ob -> ob.requiredString("rel").equals("session collection"))
                    .map(ob -> ob.requiredString("href"))
                    .findAny().get();

            result.add(new EmsConference(sessionCollection,id));
        }

        return result;
    }

    private JsonObject postData(String path,JsonObject payload) {
        try {
            HttpURLConnection conn = openConnectionToSleepingPill(path);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
                payload.toJson(printWriter);
            }
            try (InputStream is = conn.getInputStream()) {
                return JsonParser.parseToObject(is);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private boolean matchesSelectedConference(JsonObject jsonObject) {
        Set<String> conferenceList = EmsImportConfig.fetchConferences();
        if (conferenceList.isEmpty()) {
            return true;
        }
        return jsonObject.requiredArray("data").objectStream()
                .anyMatch(coob -> {
                    return coob.requiredString("name").equals("name") && conferenceList.contains(coob.requiredString("value"));
                });

    }

    public void readEmsAndSubmit(EmsConference emsConference) {
        JsonObject all = readFromEms(emsConference.sessionRef, true);
        String addTalkLoc = EmsImportConfig.serverAddress() + "/conference/" + emsConference.id + "/session";
        AtomicInteger counter = new AtomicInteger();

        all.objectValue("collection").orElse(JsonFactory.jsonObject()).arrayValue("items").orElse(JsonFactory.jsonArray()).objectStream()
                .forEach(emsses -> {
                    int num = counter.incrementAndGet();
                    if (num > EmsImportConfig.maxSessionsToImport()) {
                        return;
                    }
                    JsonObject input = dataForSession(emsses);

                    JsonObject parse = postData(addTalkLoc, input);
                    System.out.println("result from add session:");
                    System.out.println(parse);
                });
    }

    public boolean isSleepingPillEmpty() throws Exception {
        String url = EmsImportConfig.serverAddress() + "/conference";
        try (InputStream inputStream = openConnectionToSleepingPill(url).getInputStream()) {
            JsonObject jsonObject = JsonParser.parseToObject(inputStream);
            JsonArray conferences = jsonObject.requiredArray("conferences");
            return conferences.isEmpty();
        }
    }

    private HttpURLConnection openConnectionToSleepingPill(String urlpath) {
        try {
            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String sleepingpillUser = EmsImportConfig.sleepingpillUser();
            if (sleepingpillUser != null) {
                String authString = sleepingpillUser + ":" + EmsImportConfig.sleepingpillPassword();
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
        EmsImportConfig.setConfigFileName(args[0]);
        EmsImporter emsImporter = new EmsImporter(EmsImportConfig.outputFilePath());
        if (!emsImporter.isSleepingPillEmpty()) {
            System.out.println("SleepingPill is not empty. Aborting import");
            return;
        }
        List<EmsConference> emsConferences = emsImporter.readAndCreateConferences();
        emsConferences.parallelStream().forEach(emsImporter::readEmsAndSubmit);
        emsImporter.addJavaZone2017();
    }

    private void addJavaZone2017() throws Exception {
        String conferenceaddr = EmsImportConfig.serverAddress() + "/conference";
        try (InputStream is = openConnectionToSleepingPill(conferenceaddr).getInputStream()) {
            JsonArray conferences = JsonParser.parseToObject(is).requiredArray("conferences");
            if (conferences.objectStream().anyMatch(ob -> "Javazone 2017".equals(ob.requiredString("name")))) {
                return;
            }
        }

        JsonObject input = DataObjects.newConferenceObj("Javazone 2017","javazone_2017",Optional.of("30d5c2f1cb214fc8b0649a44fdf3b4bf"));

        HttpURLConnection conn = openConnectionToSleepingPill(conferenceaddr);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            input.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonObject parse = JsonParser.parseToObject(is);
            System.out.println("result from add conference:");
            System.out.println(parse);
        }

    }


}
