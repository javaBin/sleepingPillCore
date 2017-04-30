package no.java.sleepingpill.core.publicdata;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.Conference;
import no.java.sleepingpill.core.conference.ConferenceHolder;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionHolder;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PublicSessionService {
    public static PublicSessionService get() {
        return new PublicSessionService();
    }

    public ServiceResult allSessionsForConference(String conferenceSlug,Optional<String> ifModifiedSince) {
        Optional<Conference> conferenceOptional = ConferenceHolder.instance().allConferences().stream()
                .filter(co -> co.slug.equals(conferenceSlug))
                .findAny();
        if (!conferenceOptional.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unknown conference slug " + conferenceSlug);
        }
        String conferenceid = conferenceOptional.get().id;
        List<Session> allPubSessionsForConference = SessionHolder.instance().allSessions().stream()
                .filter(se -> conferenceid.equals(se.getConferenceId()))
                .filter(Session::isPublic)
                .collect(Collectors.toList());

        Optional<ServiceResult> serviceResult = checkIfModifiedSince(allPubSessionsForConference, ifModifiedSince);
        if (serviceResult.isPresent()) {
            return serviceResult.get();
        }

        JsonArray sessions = JsonArray.fromNodeStream(
                allPubSessionsForConference.stream()
                        .map(Session::asPublicSessionJson)
        );
        return ServiceResult.ok(JsonFactory.jsonObject().put("sessions",sessions));
    }

    public ServiceResult allConferences() {
        List<Conference> conferences = ConferenceHolder.instance().allConferences();
        JsonArray jsonNodes = JsonArray.fromNodeStream(conferences.stream()
                .map(conf -> JsonFactory.jsonObject().put("name", conf.name).put("slug", conf.slug))
        );
        return ServiceResult.ok(JsonFactory.jsonObject().put("conferences",jsonNodes));
    }

    private static final String IMS_FORMAT = "nnn, dd mm yyyy hh:MM:ss GMT";
    private static Optional<ServiceResult> checkIfModifiedSince(List<Session> sessions,Optional<String> ifModifiedSinceOpt) {
        if (!ifModifiedSinceOpt.isPresent()) {
            return Optional.empty();
        }
        String ifModSince = ifModifiedSinceOpt.get();
        Optional<ServiceResult> badRequestError = Optional.of(ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST, "If modified since must have format " + IMS_FORMAT));
        if (ifModSince.length() != IMS_FORMAT.length()) {
            return badRequestError;
        }
        ZonedDateTime ifModSinceTime;
        try {
            int day = parseFromFormat(ifModSince,"dd");
            int month = parseFromFormat(ifModSince,"mm");
            int year = parseFromFormat(ifModSince,"yyyy");
            int hour = parseFromFormat(ifModSince,"hh");
            int minute = parseFromFormat(ifModSince,"MM");
            int second = parseFromFormat(ifModSince,"ss");
            ifModSinceTime = LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.of("Z"));
        } catch (NumberFormatException ex) {
            return badRequestError;
        }
        for (Session session : sessions) {
            ZonedDateTime updatedZulu = LocalDateTime.parse(session.getLastUpdated()).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
            if (updatedZulu.isAfter(ifModSinceTime)) {
                return Optional.empty();
            }
        }

        return Optional.of(ServiceResult.sendError(HttpServletResponse.SC_NOT_MODIFIED,"Not modified"));

    }

    private static int parseFromFormat(String ifModSince, String code) {
        int ind = IMS_FORMAT.indexOf(code);
        return Integer.parseInt(ifModSince.substring(ind,ind+code.length()));
    }
}
