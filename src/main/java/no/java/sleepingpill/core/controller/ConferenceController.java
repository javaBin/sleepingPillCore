package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.ConferenceService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import spark.Request;
import spark.Response;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.*;

public class ConferenceController {
    private ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    public ConferenceController(){
        this(ConferenceService.instance());
    }

    public void initSpark(){
        get(HttpPaths.CONFERENCE_GET, this::getAllConferences, jsonBuddyString());
        post(HttpPaths.CONFERENCE_POST,this::postAddConference, jsonBuddyString());
        put(HttpPaths.CONFERENCE_PUT_UPDATE,this::putUpdateConference,jsonBuddyString());
    }

    public ServiceResult getAllConferences(Request req, Response res) {
        return conferenceService.allConferences();
    }

    public ServiceResult postAddConference(Request req, Response res) {
        String body = req.body();
        JsonObject payload = JsonParser.parseToObject(body);
        return conferenceService.addConference(payload);
    }


    public ServiceResult putUpdateConference(Request req, Response res) {
        String id = req.params(":id");
        String body = req.body();
        JsonObject payload = JsonParser.parseToObject(body);
        return conferenceService.updateConference(id,payload);
    }

}
