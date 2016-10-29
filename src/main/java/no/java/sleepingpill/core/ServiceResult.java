package no.java.sleepingpill.core;

import org.jsonbuddy.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ServiceResult {
    private final Optional<JsonObject> result;
    private Integer error;
    private String message;

    private ServiceResult(JsonObject result) {
        this.result = Optional.of(result);
        this.error = null;
        this.message = null;
    }

    private ServiceResult(int error, String message) {
        this.error = error;
        this.message = message;
        this.result = Optional.empty();
    }

    public static ServiceResult sendError(int error, String message) {
        return new ServiceResult(error,message);
    }

    public static ServiceResult ok(JsonObject result) {
        return new ServiceResult(result);
    }

    public Optional<JsonObject> getResult() {
        return result;
    }

    public void sendError(HttpServletResponse response) throws IOException {
        if (result.isPresent()) {
            throw new InternalError("Trying to send error when result is present");
        }
        response.sendError(error,message);
    }

    public Integer getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
