package no.java.sleepingpill.core;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public static Map<String, String> readValues(String filename) {
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

    private static String readValue(String key, String defaultValue) {
        String val = instance().values.get(key);
        return val != null ? val : defaultValue;
    }

    public static int serverPort() {
        return Integer.parseInt(readValue("serverPort","8082"));
    }


    public static boolean persistToDb() {
        return "true".equals(readValue("persistToDb","false"));
    }

    public static String dbServer() {
        return readValue("dbServer", "localhost");
    }

    public static String dbName() {
        return readValue("dbName", null);
    }


    public static String dbUser() {
        return readValue("dbUser", null);
    }

    public static String dbPassword() {
        return readValue("dbPassword", null);
    }

    public static int dbPort() {
        return Integer.parseInt(readValue("dbPort","5432"));
    }

    public static int maxDbConnections() {
        return Integer.parseInt(readValue("maxDbConnections","10"));
    }



    public static boolean cleanDb() {
        return "true".equals(readValue("cleanDb","false"));
    }



    public static String logLevel() {
        return readValue("logLevel", "DEBUG");
    }

    public static boolean useDummyConferenceHolder() {
        return "true".equals(readValue("useDummyConferenceHolder","false"));
    }



    public static String logfilePattern() {
        return readValue("logfilePattern", null);
    }


    public static int maxPictureSize() {
        return Integer.parseInt(readValue("maxPictureSize","500000"));
    }



    public static Optional<String> basicAuthLogins() {
        return Optional.ofNullable(readValue("basicAuthLogins",null));
    }

    public static Optional<String> readOnlyBasicAuthLogins() {
        return Optional.ofNullable(readValue("readOnlyBasicAuthLogins",null));
    }

    public static String serverAddress() {
        return readValue("serverAddress","http://localhost:8082");
    }
}
