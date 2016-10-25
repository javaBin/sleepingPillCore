package no.java.sleepingpill.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import no.java.sleepingpill.core.servlet.Configuration;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;


public class LoggerFactory {
    private LoggerFactory() {

    }

    private static transient Appender<ILoggingEvent> appender = null;

    public static Logger getLogger(Class<?> clazz) {
        if (Configuration.logLevel() == null) {
            return NOPLogger.NOP_LOGGER;
        }
        if (appender == null) {
            setupAppender();
        }

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(clazz);
        logger.addAppender(appender);
        logger.setLevel(Level.valueOf(Configuration.logLevel()));
        logger.setAdditive(false);





        return logger;
    }


    private static synchronized void setupAppender() {
        if (appender != null) {
            return;
        }
        LoggerContext lc = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();


        String logfilePattern = Configuration.logfilePattern();
        appender = logfilePattern == null ? setupConsoleAppender(lc, ple) : setupLogFile(lc, ple, logfilePattern);


    }





    private static ConsoleAppender<ILoggingEvent> setupConsoleAppender(LoggerContext lc, PatternLayoutEncoder ple) {
        ConsoleAppender<ILoggingEvent> newAppender = new ConsoleAppender<>();
        newAppender.setContext(lc);
        newAppender.setEncoder(ple);
        newAppender.start();
        return newAppender;
    }

    private static RollingFileAppender<ILoggingEvent> setupLogFile(LoggerContext lc, PatternLayoutEncoder ple, String loggFilePattern) {
        RollingFileAppender<ILoggingEvent> newAppender = new RollingFileAppender<>();

        newAppender.setContext(lc);
        newAppender.setEncoder(ple);

        TimeBasedRollingPolicy<Object> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setFileNamePattern(loggFilePattern);
        rollingPolicy.setParent(newAppender);
        rollingPolicy.setContext(lc);
        rollingPolicy.start();


        newAppender.setRollingPolicy(rollingPolicy);

        newAppender.start();
        return newAppender;
    }

    public static org.eclipse.jetty.util.log.Logger jettyLogger() {
        return new JettyLogger();
    }

    private static class JettyLogger implements org.eclipse.jetty.util.log.Logger {
        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public void warn(String msg, Object... args) {

        }

        @Override
        public void warn(Throwable thrown) {

        }

        @Override
        public void warn(String msg, Throwable thrown) {

        }

        @Override
        public void info(String msg, Object... args) {

        }

        @Override
        public void info(Throwable thrown) {

        }

        @Override
        public void info(String msg, Throwable thrown) {

        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void setDebugEnabled(boolean enabled) {

        }

        @Override
        public void debug(String msg, Object... args) {

        }

        @Override
        public void debug(String s, long l) {

        }

        @Override
        public void debug(Throwable thrown) {

        }

        @Override
        public void debug(String msg, Throwable thrown) {

        }

        @Override
        public org.eclipse.jetty.util.log.Logger getLogger(String name) {
            return this;
        }

        @Override
        public void ignore(Throwable ignored) {

        }
    }
}
