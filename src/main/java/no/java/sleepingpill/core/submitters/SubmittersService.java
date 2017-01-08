package no.java.sleepingpill.core.submitters;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.RegisterEmail;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.session.Session;
import no.java.sleepingpill.core.session.SessionHolder;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

public class SubmittersService {
    private static SubmittersService instance = new SubmittersService();
    public static SubmittersService instance() {
        return instance;
    }

    public synchronized ServiceResult confirmNewEmail(JsonObject addEmailObject) {
        Optional<String> email = addEmailObject.stringValue("email");
        if (!email.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Value email is required");
        }
        Optional<ConfirmedEmail> confirmedEmail = EmailHolder.instance().confirmedEmailByEmail(email.get());
        if (confirmedEmail.isPresent()) {
            return ServiceResult.ok(JsonFactory.jsonObject().put("id",confirmedEmail.get().id));
        }
        RegisterEmail registerEmail = new RegisterEmail(email.get());
        Event event = registerEmail.createEvent();
        EventHandler.instance().addEvent(event);
        JsonObject res = JsonFactory.jsonObject().put("id",registerEmail.getId());
        return ServiceResult.ok(res);
    }

    public ServiceResult allSessionsForEmail(String email) {
        List<Session> sessions = SessionHolder.instance().sessionsByEmail(email);
        JsonArray jsonArray = JsonArray.fromNodeStream(sessions.stream().map(Session::asSingleSessionJson));
        return ServiceResult.ok(JsonFactory.jsonObject().put("sessions",jsonArray));
    }
}
