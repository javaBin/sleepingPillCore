package no.java.sleepingpill.core.dao;

import no.java.sleepingpill.core.Event;
import no.java.sleepingpill.core.ServiceLocator;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DaoTest {
    @Test
    public void shouldRollback() throws Exception {
        try (ServiceLocator serviceLocator = ServiceLocator.startTransaction()) {
            serviceLocator.emsDao().addEvent(new Event("x","y"));
            serviceLocator.emsDao().rollback();
        }
        try (ServiceLocator serviceLocator = ServiceLocator.startTransaction()) {
            Assertions.assertThat(serviceLocator.emsDao().allEvents()).isEmpty();
        }

    }
}
