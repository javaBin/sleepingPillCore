package no.java.sleepingpill.core;

import org.junit.Before;

import java.lang.reflect.Method;

public abstract class CleanSetupTest {
    @Before
    public void cleanAll() throws Exception {
        Method cleanAll = ServiceLocator.class.getDeclaredMethod("cleanAll");
        cleanAll.setAccessible(true);
        cleanAll.invoke(null);
        cleanAll.setAccessible(false);
    }
}
