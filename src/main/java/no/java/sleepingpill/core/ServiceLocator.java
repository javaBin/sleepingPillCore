package no.java.sleepingpill.core;

import no.java.sleepingpill.core.conference.ConferenceHolderImpl;
import no.java.sleepingpill.core.conference.ConferenceService;
import no.java.sleepingpill.core.database.DBEventListener;
import no.java.sleepingpill.core.conference.ConferenceHolder;
import no.java.sleepingpill.core.conference.DummyConferenceHolder;
import no.java.sleepingpill.core.event.EventHandler;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.servlet.Configuration;
import no.java.sleepingpill.core.session.SessionHolder;
import no.java.sleepingpill.core.submitters.EmailHolder;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator implements AutoCloseable {
    private static final Map<Long, ServiceLocator> transactions = new HashMap<>();

    private static volatile ConferenceHolder conferenceHolder;
    private static volatile SessionHolder sessionHolder;
    private static volatile EventHandler eventHandler;
    private static volatile EmailHolder emailHolder;

    private ServiceLocator() {

    }



    public static synchronized ConferenceHolder conferenceHolder() {
        if (conferenceHolder == null) {
            conferenceHolder = Configuration.useDummyConferenceHolder() ? new DummyConferenceHolder() : new ConferenceHolderImpl();
        }
        return conferenceHolder;
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
            eventHandler.addEventListener(EmailHolder.instance());
            eventHandler.addEventListener(new DBEventListener());
            eventHandler.addEventListener(ServiceLocator.conferenceHolder());
        }
        return eventHandler;
    }

    public static synchronized EmailHolder emailHolder() {
        if (emailHolder == null) {
            emailHolder = new EmailHolder();
        }
        return emailHolder;
    }

    @SuppressWarnings("unused")
    private static void cleanAll() {
        conferenceHolder = null;
        sessionHolder = null;
        eventHandler = null;
        emailHolder = null;
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

    @Override
    public void close() {
        ServiceLocator removed;
        synchronized (transactions) {
            removed = transactions.remove(Thread.currentThread().getId());
        }
        if (removed == null) {
            throw new InternalError("Trying to close when transaction not started");
        }

    }

    public void rollback() {
    }



}
