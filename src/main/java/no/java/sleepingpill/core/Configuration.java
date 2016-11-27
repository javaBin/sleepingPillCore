package no.java.sleepingpill.core;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final String CONFIG_FILE_PROPERTY = "sleepingpillconfigfile";
    private static Configuration instance;
    private final Map<String, String> values;

    private Configuration() {
        values = readValues(System.getProperty(CONFIG_FILE_PROPERTY));
    }

    synchronized private static Configuration instance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private static Map<String, String> readValues(String filename) {
        Map<String, String> result = new HashMap<>();

        if (filename == null || filename.isEmpty()) {
            return result;
        }
        if (!(new File(filename).exists())) {
            throw new IllegalArgumentException("Configuration file does not exists: " + new File(filename).getAbsolutePath());
        }

        String doc;
        try {
            doc = toString(new FileInputStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String line : doc.split("\n")) {
            if (line.isEmpty() || line.startsWith(";") || line.startsWith("#")) {
                continue;
            }
            int pos = line.indexOf("=");
            if (pos == -1) {
                continue;
            }
            result.put(line.substring(0, pos), line.substring(pos + 1).trim());
        }

        return result;
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char) c);
            }
            return result.toString();
        }
    }

    public static int serverPort() {
        return 8082;
    }

    public static String myLocation() {
        return "http://localhost:8082/data/";
    }

    public static String dbURL() {
        return instance().readValue("dbURL", "jdbc:h2:mem:ems;DB_CLOSE_DELAY=-1");
    }

    public static String dbUser() {
        return instance().readValue("dbUser", "sa");
    }

    public static String dbPassword() {
        return instance().readValue("dbPassword", "");
    }

    public static String logLevel() {
        return instance().readValue("logLevel", "DEBUG");
    }

    public static boolean useDummyConferenceHolder() {
        return "true".equals(instance().readValue("useDummyConferenceHolder","false"));
    }

    public static String logfilePattern() {
        return instance().readValue("logfilePattern", null);
    }

    private String readValue(String key, String defaultValue) {
        String val = values.get(key);
        return val != null ? val : defaultValue;
    }


}
