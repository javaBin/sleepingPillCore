package no.java.sleepingpill.core.servlet;

import org.eclipse.jetty.http.HttpMethod;

import javax.servlet.Servlet;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputePath {

    public Optional<ServletOperation> findOperation(String pathInfo, HttpMethod httpMethod) {
        for (ServletOperation servletOperation : ServletOperation.values()) {
            if (Arrays.asList(servletOperation.httpMethod).contains(httpMethod)) {
                Pattern compile = Pattern.compile(servletOperation.pathPattern);
                Matcher matcher = compile.matcher(pathInfo);
                if (matcher.matches()) {
                    return Optional.of(servletOperation);
                }
            }
        }
        return Optional.empty();
    }

}
