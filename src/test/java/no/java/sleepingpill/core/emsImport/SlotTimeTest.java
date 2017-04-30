package no.java.sleepingpill.core.emsImport;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class SlotTimeTest {
    @Test
    public void shouldReadSlotTime() throws Exception {
        SlotTime slotTime = SlotTime.convertFromEms("2016-09-07T07:00:00Z+2016-09-07T08:00:00Z");
        assertThat(slotTime.start).isEqualTo(LocalDateTime.of(2016,9,7,9,0));
        assertThat(slotTime.end).isEqualTo(LocalDateTime.of(2016,9,7,10,0));

        slotTime = SlotTime.convertFromEms("2016-09-07T13:50:00Z+2016-09-07T14:00:00Z");
        assertThat(slotTime.start).isEqualTo(LocalDateTime.of(2016,9,7,15,50));
        assertThat(slotTime.end).isEqualTo(LocalDateTime.of(2016,9,7,16,0));
    }
}