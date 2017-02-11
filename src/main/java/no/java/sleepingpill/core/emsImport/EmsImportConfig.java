package no.java.sleepingpill.core.emsImport;

import no.java.sleepingpill.core.Configuration;

import java.util.*;

public class EmsImportConfig {
    private static String configFileName;
    private static EmsImportConfig _instance;
    private Map<String,String> values;

    public static void setConfigFileName(String configFileName) {
        EmsImportConfig.configFileName = configFileName;
    }

    private static EmsImportConfig instance() {
        if (_instance == null) {
            _instance = new EmsImportConfig();
        }
        return _instance;
    }

    private EmsImportConfig() {
        values = Configuration.readValues(configFileName);
    }



    private static String readValue(String key, String defaultValue) {
        String val = instance().values.get(key);
        return val != null ? val : defaultValue;
    }

    public static String emsUser() {
        return readValue("emsUser",null);
    }

    public static String emsPassword() {
        return readValue("emsPassword",null);
    }


    public static String outputFilePath() {
        return readValue("outputFilePath",null);
    }

    public static String serverAddress() {
        return readValue("serverAddress","http://localhost:8082/data");
    }

    public static int maxSessionsToImport() {
        String num = readValue("maxSessionsToImport",null);
        if (num == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(num);
    }


    public static Set<String> fetchConferences() {
        String val = readValue("fetchConferences",null);
        if (val == null) {
            return Collections.emptySet();
        }

        return new HashSet<>(Arrays.asList(val.split(",")));
    }

    public static String emsAddress() {
        return readValue("emaAddress","http://javazone.no/ems/server/events");
    }

    public static String sleepingpillUser() {
        return readValue("sleepingpillUser",null);
    }

    public static String sleepingpillPassword() {
        return readValue("sleepingpillPassword",null);
    }
}
