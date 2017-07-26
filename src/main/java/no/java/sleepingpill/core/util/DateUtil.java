package no.java.sleepingpill.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public static String toZuluTimeString(LocalDateTime localdate) {
        ZonedDateTime zonedDateTime = localdate.atZone(ZoneId.of("Europe/Oslo"));
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
