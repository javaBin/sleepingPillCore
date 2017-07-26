package no.java.sleepingpill.core.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.time.LocalDateTime;

public class DateUtilTest {

  @Test
  public void shouldCreateValidIso8601DateFormatInZuluTime() throws Exception {
    LocalDateTime localdate = LocalDateTime.parse("2016-09-08T07:00");
    String zuluDateTime = DateUtil.toZuluTimeString(localdate);
    assertThat(zuluDateTime, is("2016-09-08T05:00:00Z"));
  }

}