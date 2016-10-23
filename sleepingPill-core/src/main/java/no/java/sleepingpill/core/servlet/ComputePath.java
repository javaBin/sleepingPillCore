package no.java.sleepingpill.core.servlet;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputePath {
    public Optional<ServletOperation> computeGet(String pathInfo) {
        return Optional.empty();
    }

    public Optional<ServletOperation> computePost(String pathInfo) {
        if (matchesAddSession(pathInfo)) {
            return Optional.of(ServletOperation.NEW_SESSION);
        }
        return Optional.empty();
    }

    private boolean matchesAddSession(String pathInfo) {
        Pattern compile = Pattern.compile("/\\w+/session");
        Matcher matcher = compile.matcher(pathInfo);
        return matcher.matches();
    }
}
