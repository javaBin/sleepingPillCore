package no.java.sleepingpill.core.servlet;

import java.util.Optional;
import java.util.regex.Pattern;

public class ComputePath {
    public Optional<ServletOperation> computeGet(String pathInfo) {
        return Optional.empty();
    }

    public Optional<ServletOperation> computePost(String pathInfo) {
        return Optional.empty();
    }
}
