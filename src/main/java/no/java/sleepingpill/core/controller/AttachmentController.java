package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.attachment.AttachmentService;
import no.java.sleepingpill.core.util.JsonUtil;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import static no.java.sleepingpill.core.util.JsonUtil.jsonBuddyString;
import static spark.Spark.get;
import static spark.Spark.post;

public class AttachmentController {
    public void initSpark() {

        post(HttpPaths.ATTACHMENT_POST_ADD,this::addAttachment, jsonBuddyString());
        get(HttpPaths.ATTACHMENT_GET_SINGLE,this::readAttachement);
    }

    private ServiceResult readAttachement(Request request, Response response) {
        String id = request.params(":id");
        Optional<byte[]> pictureOpt = AttachmentService.get().getPicture(id);
        HttpServletResponse resp = response.raw();

        byte[] picture = pictureOpt.get();
        resp.setContentType("image/jpeg");
        resp.setContentLength(picture.length);
        try (OutputStream os = resp.getOutputStream()){
             os.write(picture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private ServiceResult addAttachment(Request request, Response response) {
        byte[] bytes = request.bodyAsBytes();
        return AttachmentService.get().addPicture(bytes);
    }


}
