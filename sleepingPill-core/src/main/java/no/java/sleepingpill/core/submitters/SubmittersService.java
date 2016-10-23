package no.java.sleepingpill.core.submitters;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.commands.RegisterEmail;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SubmittersService {
    private static SubmittersService instance = new SubmittersService();
    public static SubmittersService instance() {
        return instance;
    }

    public synchronized ServiceResult confirmNewEmail(JsonObject innkommendeJson) {
        Optional<String> email = innkommendeJson.stringValue("email");
        if (!email.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Value email is required");
        }
        Optional<ConfirmedEmail> confirmedEmail = EmailHolder.instance().confirmedEmailByEmail(email.get());
        if (confirmedEmail.isPresent()) {
            return ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Email is already confirmed");
        }
        RegisterEmail registerEmail = new RegisterEmail(email.get());
        Event event = registerEmail.createEvent();
        EventHandler.instance().addEvent(event);
        JsonObject res = JsonFactory.jsonObject().put("id",registerEmail.getId());
        return ServiceResult.ok(res);
    }

}
