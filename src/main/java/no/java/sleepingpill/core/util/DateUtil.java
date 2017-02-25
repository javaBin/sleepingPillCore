package no.java.sleepingpill.core.util;

import java.time.LocalDateTime;

public class DateUtil {
    private static DateUtil instance = null;

    public static DateUtil get() {
        if (instance == null) {
            instance = new DateUtil();
        }
        return instance;
    }

    public String generateLastUpdated() {
        return LocalDateTime.now().toString();
    }

}
