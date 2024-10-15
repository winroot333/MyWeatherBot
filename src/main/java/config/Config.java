package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties properties;
    private static final String configPath = "src/main/resources/config.properties";

    private static void loadConfig() {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            loadConfig();
        }
        return properties.getProperty(key);
    }

    private Config() {
    }
}

