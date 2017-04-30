package no.java.sleepingpill.core.emsImport;

import java.time.*;

public class SlotTime {
    public final LocalDateTime start;
    public final LocalDateTime end;

    private SlotTime(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static SlotTime convertFromEms(String slotStr) {
        int pos = slotStr.indexOf("+");
        String startstr = slotStr.substring(0,pos);
        String endstr = slotStr.substring(pos+1);

        LocalDateTime start = readToLocalTime(startstr);
        LocalDateTime end = readToLocalTime(endstr);

        return new SlotTime(start,end);
    }

    private static LocalDateTime readToLocalTime(String datestr) {
        OffsetDateTime zuluDateTime = OffsetDateTime.parse(datestr);
        ZonedDateTime norwegiantime = zuluDateTime.atZoneSameInstant(ZoneId.of("Europe/Oslo"));
        LocalDateTime localDateTime = norwegiantime.toLocalDateTime();
        return localDateTime;
    }

    @Override
    public String toString() {
        return "SlotTime{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
