package no.java.sleepingpill.core.servlet;

import java.util.Optional;
import java.util.regex.Pattern;

public class ComputePath {
    public Optional<ServletOperation> computeGet(String pathInfo) {
        return Optional.empty();
    }

    public Optional<ServletOperation> computePost(String pathInfo) {
        if (pathInfo.startsWith("/test")) {
            return Optional.of(ServletOperation.TEST);
        }
        if ("/event".equals(pathInfo)) {
            return Optional.of(ServletOperation.ADD_EVENT);
        }
        if (Pattern.compile("\\/event\\/.+\\/talk").matcher(pathInfo).matches()) {
            return Optional.of(ServletOperation.ADD_TALK);
        }
        return Optional.empty();
    }
}
