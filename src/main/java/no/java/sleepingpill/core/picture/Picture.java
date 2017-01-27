package no.java.sleepingpill.core.picture;

public class Picture {
    public final byte[] content;
    public final String contenttype;

    public Picture(byte[] content, String contenttype) {
        this.content = content;
        this.contenttype = contenttype;
    }
}
