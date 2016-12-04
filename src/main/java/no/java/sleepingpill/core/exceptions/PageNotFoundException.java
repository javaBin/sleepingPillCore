package no.java.sleepingpill.core.exceptions;


public class PageNotFoundException extends RuntimeException{
    public PageNotFoundException () {
        super("Page not found");
    }
}
