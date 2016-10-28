package no.java.sleepingpill.core.servlet;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

import static org.eclipse.jetty.http.HttpMethod.POST;
public class ComputePathTest {

    @Test
    public void shouldReadAddSession() throws Exception {
        Optional<ServletOperation> servletOperation = new ComputePath().findOperation("/event/dfgdfg/session", POST);
        Assertions.assertThat(servletOperation).contains(ServletOperation.SESSION_IN_EVENT);
    }
}