package no.java.sleepingpill.core.exceptions;

public class SessionChangedException extends RuntimeException {
    public final String correctLastChange;
    public final String actualLastChange;

    public SessionChangedException(String correctLastChange, String actualLastChange) {
        this.correctLastChange = correctLastChange;
        this.actualLastChange = actualLastChange;
    }
}
