package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.session.SessionService;
import no.java.sleepingpill.core.submitters.SubmittersService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.post;

public class SubmitterController {
    private SubmittersService submittersService;
    public SubmitterController(SubmittersService submittersService) {
        this.submittersService = submittersService;

    }

    public SubmitterController(){
        this(SubmittersService.instance());
    }

    public void initSpark(){
        post("/data/submitter", (req, res) -> {
            JsonObject payload = JsonParser.parseToObject(req.body());
            return submittersService.confirmNewEmail(payload);
        }, jsonBuddyString());
    }
}
