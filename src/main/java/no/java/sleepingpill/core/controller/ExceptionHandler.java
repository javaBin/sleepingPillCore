
package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.exceptions.ErrorMessage;
import no.java.sleepingpill.core.exceptions.PageNotFoundException;
import no.java.sleepingpill.core.exceptions.ServiceResultException;
import org.jsonbuddy.JsonValueNotPresentException;
import org.jsonbuddy.parse.JsonParseException;
import org.jsonbuddy.pojo.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static spark.Spark.*;


public class ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    public void initSparkExceptionHandling() {
        get("*", (request, response) -> {
            throw new PageNotFoundException();
        });
        post("*", (request, response) -> {
            throw new PageNotFoundException();
        });
        put("*", (request, response) -> {
            throw new PageNotFoundException();
        });
        delete("*", (request, response) -> {
            throw new PageNotFoundException();
        });

        exception(ServiceResultException.class,(e, request, response) -> {
            ServiceResultException ex = (ServiceResultException) e;
            response.status(ex.getServiceResult().getError());
            response.body(ex.getServiceResult().getMessage());
        });

        exception(JsonParseException.class, (e, request, response) -> {
            response.status(400);
            response.body(JsonGenerator.generate(new ErrorMessage(e.getMessage())).toJson());
            LOG.info("Got malformed json " + buildRequestToString(request), e);
        });

        exception(JsonValueNotPresentException.class, (e, request, response) -> {
            response.status(400);
            response.body(JsonGenerator.generate(new ErrorMessage(e.getMessage())).toJson());
            LOG.info("Missing required parameter " + buildRequestToString(request), e);
        });

        exception(PageNotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.body(JsonGenerator.generate(new ErrorMessage(e.getMessage())).toJson());
            LOG.info("Cannot find page " + buildRequestToString(request), e);
        });

        exception(Exception.class, (e, request, response) -> {
            response.status(500);
            response.body(JsonGenerator.generate(new ErrorMessage("Internal error")).toJson());
            LOG.error("Got unexpected exception " + buildRequestToString(request), e);
        });

    }

    private String buildRequestToString(Request request) {
        return String.format("Request[ method[%s] path[%s] body[%s] ip[%s] ]",
                truncate(request.requestMethod(), 10),
                truncate(request.pathInfo(), 50),
                truncate(request.body(), 50),
                request.ip());
    }

    private String truncate(String input, int maxLen) {
        return input == null ? null : input.length() > maxLen ? input.substring(0, maxLen) + "...." : input;
    }

}
