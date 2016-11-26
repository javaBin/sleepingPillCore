package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.ConferenceService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import spark.Request;
import spark.Response;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;
import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;

public class ConferenceController {
    private ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    public ConferenceController(){
        this(ConferenceService.instance());
    }

    public void initSpark(){
        get("/data/conference", this::getAllConferences, jsonBuddyString());
        post("/data/conference",this::postAddConference, jsonBuddyString());
    }

    public ServiceResult getAllConferences(Request req, Response res) {
        return conferenceService.allConferences();
    }

    public ServiceResult postAddConference(Request req, Response res) {
        String body = req.body();
        JsonObject payload = JsonParser.parseToObject(body);
        return conferenceService.addConference(payload);
    }



}
