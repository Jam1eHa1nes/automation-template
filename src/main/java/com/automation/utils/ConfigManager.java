package com.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager loads values from config.properties.
 * Any property can be overridden at runtime with a system property:
 *   mvn test -Dbrowser=firefox -Dheadless=true
 */
public class ConfigManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigManager() {}

    /**
     * Returns the value for the given key.
     * System properties take priority over config.properties values.
     */
    public static String get(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

    /**
     * Returns the value for the given key, or the defaultValue if not found.
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
