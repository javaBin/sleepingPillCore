package no.java.sleepingpill.core.emsImport;

import no.java.sleepingpill.core.session.DataField;
import no.java.sleepingpill.core.session.SessionStatus;
import no.java.sleepingpill.core.session.SessionVariables;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static no.java.sleepingpill.core.session.SessionVariables.*;

public class RoomSlotFromEmsUpdater {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Supply configfile");
            return;
        }
        EmsImportConfig.setConfigFileName(args[0]);
        RoomSlotFromEmsUpdater roomSlotFromEmsUpdater = new RoomSlotFromEmsUpdater();
        JsonArray confnodes = roomSlotFromEmsUpdater.readConferences();
        List<JsonObject> confobjects = confnodes.objectStream().collect(Collectors.toList());
        for (JsonObject confob : confobjects) {
            String confname = confob.requiredString("name");
            String confid = confob.requiredString("id");
            if ("Javazone 2017".equals(confname)) {
                continue;
            }
            System.out.println("Updating conference " + confname);
            roomSlotFromEmsUpdater.updateConference(confid);
        }
    }

    public JsonArray readConferences() {
        HttpURLConnection connection = EmsImporter.openConnectionToSleepingPill(EmsImportConfig.serverAddress() + "/conference");
        try (InputStream is = connection.getInputStream()) {
            return JsonParser.parseToObject(is).requiredArray("conferences");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateConference(String conferenceid) {
        String confaddr = EmsImportConfig.serverAddress() + "/conference/" + conferenceid + "/session";
        HttpURLConnection httpURLConnection = EmsImporter.openConnectionToSleepingPill(confaddr);

        JsonObject confObject = parseToJson(httpURLConnection);

        JsonArray sessions = confObject.requiredArray("sessions");

        sessions.objectStream()
                .filter(RoomSlotFromEmsUpdater::shouldBeGivenRoomAndSlotFromEms)
                .forEach(RoomSlotFromEmsUpdater::updateSession);

    }

    private static JsonObject parseToJson(HttpURLConnection httpURLConnection) {
        try (InputStream is = httpURLConnection.getInputStream()) {
            return JsonParser.parseToObject(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean shouldBeGivenRoomAndSlotFromEms(JsonObject sessionObj) {
        if (!SessionStatus.HISTORIC.toString().equals(sessionObj.requiredString(SESSION_STATUS))) {
            return false;
        }
        return sessionObj.requiredObject(DATA_OBJECT).objectValue(EMS_LOCATION).isPresent();
    }

    private static void updateSession(JsonObject sessionObj) {
        String emsloc = sessionObj.requiredObject(DATA_OBJECT).requiredObject(EMS_LOCATION).requiredString(VALUE_KEY);
        HttpURLConnection connection = EmsImporter.openConnectionToEms(emsloc, false);
        JsonObject emsobj = parseToJson(connection);
        JsonArray emsLinks = emsobj.requiredObject("collection")
                .requiredArray("items")
                .get(0, JsonObject.class)
                .requiredArray("links");

        Optional<String> room = linkWithRel(emsLinks,"room item").map(ob -> ob.requiredString("prompt"));
        Optional<SlotTime> slot = linkWithRel(emsLinks,"slot item")
                .map(ob -> ob.requiredString("prompt"))
                .map(SlotTime::convertFromEms);
        Optional<String> video = linkWithRel(emsLinks,"alternate video").map(ob -> ob.requiredString("href"));


        String lastUpdated = sessionObj.requiredString(SessionVariables.LAST_UPDATED);
        String sessionid = sessionObj.requiredString("id");

        JsonObject dataObject = JsonFactory.jsonObject();

        room.ifPresent(ro -> dataObject.put(ROOM, DataField.simplePublicStringValue(ro).jsonValue()));
        video.ifPresent(vid -> dataObject.put(VIDEO, DataField.simplePublicStringValue(vid).jsonValue()));
        slot.ifPresent(slotTime -> {
            dataObject.put(START_TIME,DataField.simplePublicStringValue(slotTime.start.toString()).jsonValue());
            dataObject.put(END_TIME,DataField.simplePublicStringValue(slotTime.end.toString()).jsonValue());
        });


        JsonObject updateobj = JsonFactory.jsonObject().put("data", dataObject).put(SessionVariables.LAST_UPDATED, lastUpdated);

        String updateAddr = EmsImportConfig.serverAddress() + "/session/" + sessionid;
        try {
            putToSleepingPill(updateAddr,updateobj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(String.format("Updated id %s with %s",sessionid,updateobj.toJson()));

    }

    private static void putToSleepingPill(String updateAddr, JsonObject updateobj) throws IOException {
        HttpURLConnection conn = EmsImporter.openConnectionToSleepingPill(updateAddr);
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            updateobj.toJson(printWriter);
        }
        try (InputStream is = conn.getInputStream()) {
            JsonParser.parseToObject(is);
        }
    }


    private static Optional<JsonObject> linkWithRel(JsonArray emsLinks,String rel) {
        return emsLinks.objectStream()
                .filter(ob -> ob.requiredString("rel").equals(rel))
                .findAny();
    }
}
