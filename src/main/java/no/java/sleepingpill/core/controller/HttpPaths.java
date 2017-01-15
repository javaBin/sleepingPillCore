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


    private HttpPaths() {
        throw new RuntimeException("Illegal state");
    }
}
