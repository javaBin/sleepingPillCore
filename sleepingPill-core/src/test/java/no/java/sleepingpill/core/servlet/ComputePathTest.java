package no.java.sleepingpill.core.servlet;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ComputePathTest {

    @Test
    public void shouldReadAddSession() throws Exception {
        Optional<ServletOperation> servletOperation = new ComputePath().computePost("/dfgdfg/session/");
        Assertions.assertThat(servletOperation).contains(ServletOperation.NEW_SESSION);
    }
}