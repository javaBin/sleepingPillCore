package no.java.sleepingpill.core.controller;

import no.java.sleepingpill.core.Configuration;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spark.Spark.before;
import static spark.Spark.halt;

public class BasicAuthController {
    private final Set<Credentials> allowedLogins;

    public BasicAuthController() {
        this.allowedLogins = readAllowedLogins();
    }

    private static Set<Credentials> readAllowedLogins() {
        Optional<String> basicAuthLogins = Configuration.basicAuthLogins();
        if (!basicAuthLogins.isPresent()) {
            return Collections.emptySet();
        }
        return Stream.of(basicAuthLogins.get().split(","))
                .map(str -> {
                    int pos = str.indexOf("=");
                    return new Credentials(str.substring(0,pos),str.substring(pos+1));
                })
                .collect(Collectors.toSet());
    }

    public void initSpark() {
        before(this::basicAuthFilter);
    }

    private void basicAuthFilter(Request request, Response response) {
        if (this.allowedLogins.isEmpty()) {
            return;
        }
        Optional<Credentials> credentials = credentialsWithBasicAuthentication(request);
        if (!credentials.isPresent() || !allowedLogins.contains(credentials.get())) {
            halt(401,"Wrong basic auth");
        }

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

                            return Optional.of(new Credentials(login,password));
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
