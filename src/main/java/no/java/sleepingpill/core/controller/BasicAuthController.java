package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.Configuration;
import spark.Request;
import spark.Response;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spark.Spark.before;
import static spark.Spark.halt;

public class BasicAuthController {
    private final Set<Credentials> allowedLogins;
    private final Set<Credentials> readOnlyLogins;

    public BasicAuthController() {
        this.allowedLogins = getCredentials(Configuration.basicAuthLogins());
        this.readOnlyLogins = getCredentials(Configuration.readOnlyBasicAuthLogins());
    }

    private static Set<Credentials> getCredentials(Optional<String> basicAuthLogins) {
        if (!basicAuthLogins.isPresent()) {
            return Collections.emptySet();
        }
        return Stream.of(basicAuthLogins.get().split(","))
                .map(str -> {
                    int pos = str.indexOf("=");
                    return new Credentials(str.substring(0, pos), str.substring(pos + 1));
                })
                .collect(Collectors.toSet());
    }

    public void initSpark() {
        before("/data/*", this::basicAuthFilter);
    }

    private void basicAuthFilter(Request request, Response response) {

        if (this.allowedLogins.isEmpty() && this.readOnlyLogins.isEmpty()) {
            return;
        }

        Optional<Credentials> credentials = credentialsWithBasicAuthentication(request);


        if (!isAuthorized(credentials,request)) {
            halt(401, "Not authorized");
        }
    }

    private boolean isAuthorized(Optional<Credentials> credentials, Request request) {
        if (!credentials.isPresent()) {
            return false;
        }
        if (allowedLogins.contains(credentials.get())) {
            return true;
        }
        if (!isReadRequest(request)) {
            return false;
        }
        return readOnlyLogins.contains(credentials.get());
    }

    private boolean isReadRequest(Request request) {
        return "GET".equalsIgnoreCase(request.requestMethod()) || "HEAD".equalsIgnoreCase(request.requestMethod());
    }

    private static class Credentials {
        public final String login;
        public final String password;

        public Credentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Credentials that = (Credentials) o;
            return Objects.equals(login, that.login) &&
                    Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(login, password);
        }
    }

    private Optional<Credentials> credentialsWithBasicAuthentication(Request req) {
        String authHeader = req.headers("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        String credentials = new String(Base64.getDecoder().decode(st.nextToken()), "UTF-8");
                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            String login = credentials.substring(0, p).trim();
                            String password = credentials.substring(p + 1).trim();

                            return Optional.of(new Credentials(login, password));
                        } else {
                            return Optional.empty();
                        }
                    } catch (UnsupportedEncodingException e) {
                        return Optional.empty();
                    }
                }
            }
        }

        return Optional.empty();
    }
}
