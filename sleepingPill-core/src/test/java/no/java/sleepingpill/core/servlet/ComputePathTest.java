package no.java.sleepingpill.core.servlet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComputePathTest {
    private ComputePath computePath = new ComputePath();

    @Test
    public void shouldAddEvent() throws Exception {
        assertThat(computePath.computePost("/event")).contains(ServletOperation.ADD_EVENT);
    }

    @Test
    public void shouldAddTalk() throws Exception {
        assertThat(computePath.computePost("/event/fdhfsdgfds/talk")).contains(ServletOperation.ADD_TALK);
    }
}