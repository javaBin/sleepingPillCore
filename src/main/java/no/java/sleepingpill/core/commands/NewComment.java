package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.exceptions.ServiceResultException;
import no.java.sleepingpill.core.session.SessionVariables;
import no.java.sleepingpill.core.util.DateUtil;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import javax.servlet.http.HttpServletResponse;

import static no.java.sleepingpill.core.session.SessionVariables.*;

public class NewComment {
    private final String email;
    private final String from;
    private final String comment;

    public NewComment(JsonObject commentObj) {
        this.email = commentObj.stringValue(COMMENT_EMAIL).orElseThrow(() -> missing(COMMENT_EMAIL));
        this.from = commentObj.stringValue(SessionVariables.COMMENT_FROM).orElseThrow(() -> missing(COMMENT_FROM));
        this.comment = commentObj.stringValue(SessionVariables.COMMENT_COMMENT).orElseThrow(() -> missing(COMMENT_COMMENT));
    }

    public NewComment(String email, String from, String comment) {
        this.email = email;
        this.from = from;
        this.comment = comment;
    }

    private ServiceResultException missing(String field) {
        return new ServiceResultException(ServiceResult.sendError(HttpServletResponse.SC_BAD_REQUEST,"Comment object missing field " + field));
    }


    public JsonObject eventData() {
        return JsonFactory.jsonObject()
                .put(COMMENT_ID, IdGenerator.newId())
                .put(COMMENT_POSTEDDATE, DateUtil.get().generateLastUpdated())
                .put(COMMENT_FROM,from)
                .put(COMMENT_EMAIL,email)
                .put(COMMENT_COMMENT,comment);
    }
}
