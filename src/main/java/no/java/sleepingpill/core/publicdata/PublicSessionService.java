package no.java.sleepingpill.core.publicdata;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.conference.Conference;
import no.java.sleepingpill.core.conference.ConferenceHolder;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionHolder;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

public class PublicSessionService {
    public static PublicSessionService get() {
        return new PublicSessionService();
    }

    public ServiceResult allSessionsForConference(String conferenceSlug) {
        Optional<Conference> conferenceOptional = ConferenceHolder.instance().allConferences().stream()
                .filter(co -> co.slug.equals(conferenceSlug))
                .findAny();
        if (!conferenceOptional.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unknown conference slug " + conferenceSlug);
        }
        String conferenceid = conferenceOptional.get().id;
        JsonArray sessions = JsonArray.fromNodeStream(
                SessionHolder.instance().allSessions().stream()
                        .filter(se -> conferenceid.equals(se.getConferenceId()))
                        .filter(Session::isPublic)
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
}
