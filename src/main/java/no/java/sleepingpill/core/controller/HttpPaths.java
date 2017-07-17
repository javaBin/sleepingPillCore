package no.java.sleepingpill.core.controller;

public class HttpPaths {
    public static final String CONFERENCE_GET = "/data/conference";
    public static final String CONFERENCE_POST = "/data/conference";
    public static final String SESSION_GET_ALL_FOR_CONFERENCE_BY_ID = "/data/session";
    public static final String SESSION_GET_ALL_BY_CONFERENCE = "/data/conference/:conferenceId/session";
    public static final String SESSION_GET_SINGLE = "/data/session/:id";
    public static final String SESSION_POST_ADD_NEW = "/data/conference/:conferenceId/session";
    public static final String SESSION_PUT_UPDATE = "/data/session/:id";
    public static final String SESSION_DELETE = "/data/session/:id";
    public static final String SUBMITTER_POST_ADD_NEW = "/data/submitter";
    public static final String SUBMITTER_GET_ALL_SESSIONS = "/data/submitter/:email/session";
    public static final String PICTURE_POST_ADD = "/data/picture";
    public static final String PICTURE_GET_SINGLE = "/data/picture/:id";
    public static final String PUBLIC_GET_SESSION_FOR_CONFERENCE = "/public/allSessions/:slug";
    public static final String PUBLIC_GET_CONFERENCES = "/public/allSessions";
    public static final String PUBLIC_GET_PICTURE = "/public/picture/:id";


    private HttpPaths() {
        throw new RuntimeException("Illegal state");
    }
}
