package no.java.sleepingpill.core.servlet;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputePath {
    public Optional<ServletOperation> computeGet(String pathInfo) {
        if ("/event".equals(pathInfo)) {
            return Optional.of(ServletOperation.ALL_ARRANGED_EVENTS);
        }
        if (matchesOneSession(pathInfo)) {
            return Optional.of(ServletOperation.SESSION_BY_ID);
        }
        return Optional.empty();
    }

    private boolean matchesOneSession(String pathInfo) {
        Pattern compile = Pattern.compile("/session/\\w+");
        Matcher matcher = compile.matcher(pathInfo);
        return matcher.matches();

    }

    public Optional<ServletOperation> computePost(String pathInfo) {
        if (matchesAddSession(pathInfo)) {
            return Optional.of(ServletOperation.NEW_SESSION);
        }
        return Optional.empty();
    }

    private boolean matchesAddSession(String pathInfo) {
        Pattern compile = Pattern.compile("/event/\\w+/session");
        Matcher matcher = compile.matcher(pathInfo);
        return matcher.matches();
    }

    public Optional<ServletOperation> computePut(String pathInfo) {
        if (matchesOneSession(pathInfo)) {
            return Optional.of(ServletOperation.UPDATE_SESSION);
        }
        return Optional.empty();
    }
}
