package no.java.sleepingpill.core;

import no.java.sleepingpill.core.controller.ConferenceController;
import no.java.sleepingpill.core.controller.SessionController;
import no.java.sleepingpill.core.controller.SubmitterController;
import no.java.sleepingpill.core.event.Conference;

import static spark.Spark.port;
public class SparkStart {
    public static void main(String[] args) {
        port(8082);
        new SubmitterController().initSpark();
        new ConferenceController().initSpark();
        new SessionController().initSpark();
    }
}
