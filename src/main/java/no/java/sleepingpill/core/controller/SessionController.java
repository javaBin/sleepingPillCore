package no.java.sleepingpill.core.controller;


import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.session.SessionService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import spark.Request;
import spark.Response;

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
        get(HttpPaths.SESSION_GET_ALL_FOR_CONFERENCE_BY_ID, this::getAllSessionsForConference, jsonBuddyString());

        get(HttpPaths.SESSION_GET_ALL_BY_CONFERENCE, this::getAllSessionsForConferenceSession, jsonBuddyString());

        get(HttpPaths.SESSION_GET_SINGLE, this::getSessionById, jsonBuddyString());

        post(HttpPaths.SESSION_POST_ADD_NEW, this::postAddSession, jsonBuddyString());

        put(HttpPaths.SESSION_PUT_UPDATE, this::putUpdateSession, jsonBuddyString());

        after((req, res) -> {
            res.type("application/json");
        });

    }


    public ServiceResult getAllSessionsForConference(Request req, Response res) {
        String id = req.queryParams("conferenceId");
        return sessionService.allSessionsForConference(id);
    }

    public ServiceResult getAllSessionsForConferenceSession(Request req, Response res) {
        String confereceId = req.params(":conferenceId");
        return sessionService.allSessionsForConference(confereceId);
    }

    public ServiceResult getSessionById(Request req, Response res) {
        String id = req.params(":id");
        return sessionService.sessionById(id);
    }

    public ServiceResult postAddSession(Request req, Response res) {
        String confereceId = req.params(":conferenceId");
        JsonObject payload = JsonParser.parseToObject(req.body());
        return sessionService.addSession(confereceId, payload);
    }

    public ServiceResult putUpdateSession(Request req, Response res) {
        String id = req.params(":id");
        JsonObject payload = JsonParser.parseToObject(req.body());
        return sessionService.updateSession(id, payload);
    }

}
