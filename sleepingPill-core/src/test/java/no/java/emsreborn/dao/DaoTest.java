package no.java.emsreborn.dao;

import no.java.emsreborn.Event;
import no.java.emsreborn.ServiceLocator;
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
            assertThat(serviceLocator.emsDao().allEvents()).isEmpty();
        }

    }
}
