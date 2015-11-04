package no.java.emsreborn;

import no.java.emsreborn.dao.EmsDao;
import no.java.emsreborn.dao.InMemEmsDao;
import no.java.emsreborn.exceptions.InternalError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ServiceLocator implements AutoCloseable {
    private static final Map<Long,ServiceLocator> transactions = new HashMap<>();
    private EmsDao emsDao;

    @Override
    public void close()  {
        if (emsDao != null) {
            emsDao.close();
        }
        emsDao = null;
        ServiceLocator removed = transactions.remove(Thread.currentThread().getId());
        if (removed == null) {
            throw new InternalError("Trying to close when transaction not started");
        }

    }

    private ServiceLocator() {

    }

    public static ServiceLocator startTransaction() {
        long id = Thread.currentThread().getId();
        if (transactions.containsKey(id)) {
            throw new InternalError("Transaction already started");
        }
        ServiceLocator serviceLocator = new ServiceLocator();
        transactions.put(id,serviceLocator);
        return serviceLocator;
    }

    public static ServiceLocator instance() {
        return Optional.ofNullable(transactions.get(Thread.currentThread().getId())).orElseThrow(throwInternalError("Transaction has not started"));
    }

    private static Supplier<no.java.emsreborn.exceptions.InternalError> throwInternalError(String message) {
        return () -> new InternalError(message);
    }

    public EmsDao emsDao() {
        if (emsDao == null) {
            emsDao = new InMemEmsDao();
        }
        return emsDao;
    }

    public void rollback() {
        if (emsDao != null) {
            emsDao.rollback();
        }
    }
}
