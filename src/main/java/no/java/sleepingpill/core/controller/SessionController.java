package no.java.sleepingpill.core.controller;


import no.java.sleepingpill.core.session.SessionService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class SessionController {
    private SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    public SessionController() {
        this(SessionService.instance());
    }

    public void initSpark(){
        get("/data/session", (req, res) -> {
            String id = req.queryParams("conferenceId");
            return sessionService.allSessionsForConference(id);
        }, jsonBuddyString());

        get("/data/conference/:conferenceId/session", (req, res) -> {
            String confereceId = req.params(":conferenceId");
            return sessionService.allSessionsForConference(confereceId);
        }, jsonBuddyString());

        get("/data/session/:id", (req, res) -> {
            String id = req.params(":id");
            return sessionService.sessionById(id);
        }, jsonBuddyString());

        post("/data/conference/:conferenceId/session", (req, res) -> {
            String confereceId = req.params(":conferenceId");
            JsonObject payload = JsonParser.parseToObject(req.body());
            return sessionService.addSession(confereceId, payload);
        }, jsonBuddyString());

        put("/data/session/:id", (req, res) -> {
            String id = req.params(":id");
            JsonObject payload = JsonParser.parseToObject(req.body());
            return sessionService.updateSession(id, payload);
        }, jsonBuddyString());

        after((req, res) -> {
            res.type("application/json");
        });

    }
}
