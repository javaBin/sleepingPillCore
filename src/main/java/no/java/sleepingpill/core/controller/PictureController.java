package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.exceptions.ServiceResultException;
import no.java.sleepingpill.core.picture.Picture;
import no.java.sleepingpill.core.picture.PicureService;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.get;
import static spark.Spark.post;

public class PictureController {
    public void initSpark() {

        post(HttpPaths.PICTURE_POST_ADD,this::addPicture, jsonBuddyString());
        get(HttpPaths.PICTURE_GET_SINGLE,this::readPicture);
    }

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

    private ServiceResult addPicture(Request request, Response response) {
        byte[] bytes = request.bodyAsBytes();
        String contentType = Optional.ofNullable(request.contentType()).orElse("image/jpeg");
        return PicureService.get().addPicture(new Picture(bytes,contentType));
    }


}
