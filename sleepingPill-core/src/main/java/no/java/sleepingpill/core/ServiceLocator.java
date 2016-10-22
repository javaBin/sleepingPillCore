package no.java.sleepingpill.core;

import no.java.sleepingpill.core.event.ArrangedEventHolder;
import no.java.sleepingpill.core.event.DummyArrangedEventHolder;
import no.java.sleepingpill.core.event.Event;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.session.SessionHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ServiceLocator implements AutoCloseable {
    private static final Map<Long,ServiceLocator> transactions = new HashMap<>();

    private static volatile ArrangedEventHolder arrangedEventHolder;
    private static volatile SessionHolder sessionHolder;
    private static volatile EventHandler eventHandler;

    public static synchronized ArrangedEventHolder arrangedEventHolder() {
        if (arrangedEventHolder == null) {
            arrangedEventHolder = new DummyArrangedEventHolder();
        }
        return arrangedEventHolder;
    }

    public static synchronized SessionHolder sessionHolder() {
        if (sessionHolder == null) {
            sessionHolder = new SessionHolder();
        }
        return sessionHolder;
    }

    public static synchronized EventHandler eventHandler() {
        if (eventHandler == null) {
            eventHandler = new EventHandler();
            eventHandler.addEventListener(SessionHolder.instance());
        }
        return eventHandler;
    }

    @SuppressWarnings("unused")
    private static void cleanAll() {
        arrangedEventHolder = null;
        sessionHolder = null;
        eventHandler = null;
    }



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
