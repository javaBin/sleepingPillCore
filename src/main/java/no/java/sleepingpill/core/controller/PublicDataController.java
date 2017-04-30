package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.publicdata.PublicSessionService;
import org.jsonbuddy.JsonFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;

public class PublicDataController {

    public void initSpark() {
        Spark.get(HttpPaths.PUBLIC_GET_SESSION_FOR_CONFERENCE,this::sessionsForConference,jsonBuddyString());
        Spark.get(HttpPaths.PUBLIC_GET_CONFERENCES,this::allConferences,jsonBuddyString());
    }

    public ServiceResult sessionsForConference(Request req, Response res) {
        Optional<String> ifModifiedSince = Optional.ofNullable(req.headers("If-Modified-Since"));
        String conferenceSlug = req.params(":slug");
        return PublicSessionService.get().allSessionsForConference(conferenceSlug,ifModifiedSince);
    }

    public ServiceResult allConferences(Request req, Response res) {
        return PublicSessionService.get().allConferences();
    }


}
