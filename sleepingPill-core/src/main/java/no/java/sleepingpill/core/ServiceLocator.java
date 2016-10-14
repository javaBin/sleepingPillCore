package no.java.sleepingpill.core;

import no.java.sleepingpill.core.exceptions.InternalError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ServiceLocator implements AutoCloseable {
    private static final Map<Long,ServiceLocator> transactions = new HashMap<>();

    @Override
    public void close()  {
        ServiceLocator removed;
        synchronized (transactions) {
            removed = transactions.remove(Thread.currentThread().getId());
        }
        if (removed == null) {
            throw new InternalError("Trying to close when transaction not started");
        }

    }

    private ServiceLocator() {

    }

    public static ServiceLocator startTransaction() {
        long id = Thread.currentThread().getId();
        synchronized (transactions) {
            if (transactions.containsKey(id)) {
                throw new InternalError("Transaction already started");
            }
        }
        ServiceLocator serviceLocator = new ServiceLocator();
        synchronized (transactions) {
            transactions.put(id, serviceLocator);
        }
        return serviceLocator;
    }

    public static ServiceLocator instance() {
        synchronized (transactions) {
            return Optional.ofNullable(transactions.get(Thread.currentThread().getId())).orElseThrow(throwInternalError("Transaction has not started"));
        }
    }

    private static Supplier<InternalError> throwInternalError(String message) {
        return () -> new InternalError(message);
    }


    public void rollback() {
    }
}
