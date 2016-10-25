package no.java.sleepingpill.core.servlet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final String CONFIG_FILE_PROPERTY = "sleepingpillconfigfile";
    private static Configuration instance;


    synchronized private static Configuration instance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private final Map<String, String> values;

    private Configuration() {
        values = readValues(System.getProperty(CONFIG_FILE_PROPERTY));
    }

    private static Map<String, String> readValues(String filename) {
        Map<String, String> result = new HashMap<>();

        if (filename == null || filename.isEmpty() || !(new File(filename).exists())) {
            return result;
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

    private String readValue(String key, String defaultValue) {
        String val = values.get(key);
        return val != null ? val : defaultValue;
    }



    public static int serverPort() {
        return 8082;
    }
    public static String myLocation() {
        return "http://localhost:8082/data/";
    }

    public static String dbServer() {
        return instance().readValue("dbServer", "localhost");
    }

    public static String dbName() {
        return instance().readValue("dbName", "sleeppill");
    }

    public static String dbUser() {
        return instance().readValue("dbUser", "postgres");
    }

    public static String dbPassword() {
        return instance().readValue("dbPassword", "bingo");
    }

    public static String logLevel() {
        return instance().readValue("logLevel", "DEBUG");
    }

    public static String logfilePattern() {
        return instance().readValue("logfilePattern", null);
    }


}
