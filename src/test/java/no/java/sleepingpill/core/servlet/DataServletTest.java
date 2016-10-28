package no.java.sleepingpill.core.servlet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataServletTest {

    @Before
    public void setUp() throws InterruptedException{
        Thread.sleep(2000);
    }
    @Test
    public void extractGroup_state_result() throws Exception {
        DataServlet.extractGroup(ServletOperation.SESSION_IN_EVENT, "/event/6c599656fdd846468bbcab66cfffbbc0/session", 1);
    }
}