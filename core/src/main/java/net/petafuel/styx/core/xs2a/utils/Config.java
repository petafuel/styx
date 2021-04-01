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
        if (Paths.get(PROP_FILENAME).toFile().exists()) {
            try (InputStream stream = new FileInputStream(PROP_FILENAME)) {
                properties.load(stream);
            } catch (FileNotFoundException e) {
                LOG.error(String.format("Properties file not found: %s", e.getMessage()));
            } catch (IOException e) {
                LOG.error(String.format("Exception in getting properties file: %s", e.getMessage()));
            }
        } else {
            try {
                InputStream stream = Config.class.getClassLoader().getResourceAsStream(PROP_FILENAME);

                // loads all properties of the config.properties - file
                if (stream != null) {
                    properties.load(stream);
                } else {
                    throw new IOException("Unable to load " + PROP_FILENAME + " from file or resource stream");
                }
            } catch (FileNotFoundException e) {
                LOG.error(String.format("Properties file not found: %s", e.getMessage()));
            } catch (IOException e) {
                LOG.error(String.format("Exception in getting properties file: %s", e.getMessage()));
            }
        }

        System.getProperties().putAll(properties);
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
