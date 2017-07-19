package no.java.sleepingpill.core.session;

import no.java.sleepingpill.core.util.DateUtil;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import static no.java.sleepingpill.core.session.SessionVariables.*;

public class Comment {
    private final String id;
    private final String postedDate;
    private final String email;
    private final String from;
    private final String comment;

    private Comment(String email, String from, String comment) {
        this.email = email;
        this.from = from;
        this.comment = comment;
        this.id = IdGenerator.newId();
        this.postedDate = DateUtil.get().generateLastUpdated();
    }

    private Comment(JsonObject jsonObject) {
        this.id = jsonObject.requiredString(COMMENT_ID);
        this.postedDate = jsonObject.requiredString(COMMENT_POSTEDDATE);
        this.email = jsonObject.requiredString(COMMENT_EMAIL);
        this.from = jsonObject.requiredString(COMMENT_FROM);
        this.comment = jsonObject.requiredString(COMMENT_COMMENT);
    }

    public Comment(Comment othercomment) {
        this.id = othercomment.id;
        this.postedDate = othercomment.postedDate;
        this.email = othercomment.email;
        this.from = othercomment.from;
        this.comment = othercomment.comment;

    }



    public static Comment fromJson(JsonObject jsonObject) {
        return new Comment(jsonObject);
    }

    public JsonObject toJson() {
        return JsonFactory.jsonObject()
                .put(COMMENT_ID,id)
                .put(COMMENT_POSTEDDATE,postedDate)
                .put(COMMENT_EMAIL,email)
                .put(COMMENT_FROM,from)
                .put(COMMENT_COMMENT,comment)
                ;
    }

    public String getId() {
        return id;
    }


}
