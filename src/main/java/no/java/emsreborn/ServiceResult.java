package no.java.emsreborn;

import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class ServiceResult {
    private final Optional<JsonObject> result;

    private ServiceResult(JsonObject result) {
        this.result = Optional.of(result);
    }

    private ServiceResult(int error, String message) {
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
}
