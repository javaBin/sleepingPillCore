package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.submitters.SubmittersService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import spark.Request;
import spark.Response;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.get;
import static spark.Spark.post;

public class SubmitterController {
    private final SubmittersService submittersService;
    public SubmitterController(SubmittersService submittersService) {
        this.submittersService = submittersService;

    }

    public SubmitterController(){
        this(SubmittersService.instance());
    }

    public void initSpark(){
        post(HttpPaths.SUBMITTER_POST_ADD_NEW, this::confirmEmail, jsonBuddyString());
        get(HttpPaths.SUBMITTER_GET_ALL_SESSIONS,this::allSessionsForEmail,jsonBuddyString());
    }


    public ServiceResult confirmEmail(Request req, Response res) {
        JsonObject payload = JsonParser.parseToObject(req.body());
        return submittersService.confirmNewEmail(payload);
    }

    public ServiceResult allSessionsForEmail(Request req, Response res) {
        String email = req.params(":email");
        return submittersService.allSessionsForEmail(email);

    }


}
