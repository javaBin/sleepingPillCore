package no.java.sleepingpill.core.servlet;

import org.eclipse.jetty.http.HttpMethod;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpMethod.POST;
import static org.eclipse.jetty.http.HttpMethod.PUT;

public enum ServletOperation {
    SESSION_BY_ID("^/session/\\w+$", GET, PUT),
    SESSION_IN_EVENT("^/event/\\w+/session$", POST),
    ALL_EVENTS("^/event$", GET);

    public final HttpMethod[] httpMethod;
    public final String pathPattern;

    ServletOperation(String pathPattern, HttpMethod... httpMethod) {
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }

}
