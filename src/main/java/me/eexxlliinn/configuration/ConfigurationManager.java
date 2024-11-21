package me.eexxlliinn.configuration;

import me.eexxlliinn.connection.ConnectionManager;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationManager {

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ConnectionManager.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
