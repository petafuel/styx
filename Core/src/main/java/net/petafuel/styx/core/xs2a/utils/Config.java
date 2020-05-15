package net.petafuel.styx.core.xs2a.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static final Logger LOG = LogManager.getLogger(Config.class);

    private static final String PROP_FILENAME = "core.properties";
    private static Config singletonInstance;
    private final Properties properties;

    private Config() {
        properties = new Properties();

        InputStream stream;
        try {
            if (Paths.get(PROP_FILENAME).toFile().exists()) {
                stream = new FileInputStream(PROP_FILENAME);
            } else {
                stream = Config.class.getClassLoader().getResourceAsStream(PROP_FILENAME);
            }
            properties.load(stream); // loads all properties of the config.properties - file
        } catch (FileNotFoundException e) {
            LOG.error("Properties file not found: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("Exception in getting properties file: " + e.getMessage());
        }
    }

    public static Config getInstance() {
        if (Config.singletonInstance == null) {
            Config.singletonInstance = new Config();
        }
        return Config.singletonInstance;
    }

    public Properties getProperties() {
        return properties;
    }
}
