package no.java.sleepingpill.core.util;

import java.util.Arrays;
import java.util.UUID;

public class IdGenerator {
    public static String newId() {
        return UUID.randomUUID().toString();
    }

}
