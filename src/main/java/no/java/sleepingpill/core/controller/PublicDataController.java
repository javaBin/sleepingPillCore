package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.exceptions.ServiceResultException;
import no.java.sleepingpill.core.picture.Picture;
import no.java.sleepingpill.core.picture.PicureService;
import no.java.sleepingpill.core.publicdata.PublicSessionService;
import org.jsonbuddy.JsonFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.get;

public class PublicDataController {

    public void initSpark() {
        Spark.get(HttpPaths.PUBLIC_GET_SESSION_FOR_CONFERENCE,this::sessionsForConference,jsonBuddyString());
        Spark.get(HttpPaths.PUBLIC_GET_CONFERENCES,this::allConferences,jsonBuddyString());
        Spark.get(HttpPaths.PUBLIC_GET_PICTURE,this::readPicture);
        Spark.get(HttpPaths.PUBLIC_GET_SESSIONS_BY_CONFERENCE_ID,this::sessionsByConferenceid,jsonBuddyString());
    }

    private ServiceResult sessionsByConferenceid(Request request, Response response) {
        Optional<String> ifModifiedSince = Optional.ofNullable(request.headers("If-Modified-Since"));
        String conferenceid = request.params(":id");
        response.header("Access-Control-Allow-Origin","*");
        return PublicSessionService.get().allSessionsForConferenceById(conferenceid,ifModifiedSince);
    }

    public ServiceResult sessionsForConference(Request req, Response res) {
        Optional<String> ifModifiedSince = Optional.ofNullable(req.headers("If-Modified-Since"));
        String conferenceSlug = req.params(":slug");
        res.header("Access-Control-Allow-Origin","*");
        return PublicSessionService.get().allSessionsForConference(conferenceSlug,ifModifiedSince);
    }

    public ServiceResult allConferences(Request req, Response res) {
        res.header("Access-Control-Allow-Origin","*");
        return PublicSessionService.get().allConferences();
    }

    @SuppressWarnings("Duplicates")
    private Void readPicture(Request request, Response response) {
        String id = request.params(":id");
        Optional<Picture> pictureOpt = PicureService.get().getPicture(id);

        if (!pictureOpt.isPresent()) {
            throw new ServiceResultException(ServiceResult.sendError(HttpServletResponse.SC_NOT_FOUND,"Unknown picture id " + id));
        }

        HttpServletResponse resp = response.raw();
        Picture picture = pictureOpt.get();
        resp.setContentType(picture.contenttype);
        resp.setContentLength(picture.content.length);
        try (OutputStream os = resp.getOutputStream()){
            os.write(picture.content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
