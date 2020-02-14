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

    public Properties getProperties() {
        return config;
    }

    private Properties config;
    private static Config singletonInstance;
    private static final String PROP_FILENAME = "core.properties";

    public static Config getInstance() {
        if (Config.singletonInstance == null) {
            Config.singletonInstance = new Config();
        }
        return Config.singletonInstance;
    }

    private Config() {
        config = new Properties();

        InputStream stream;
        try {
            if (Paths.get(PROP_FILENAME).toFile().exists()) {
                stream = new FileInputStream(PROP_FILENAME);
            } else {
                stream = Config.class.getClassLoader().getResourceAsStream(PROP_FILENAME);
            }
            config.load(stream); // loads all properties of the config.properties - file
        } catch (FileNotFoundException e) {
            LOG.error("Properties file not found: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("Exception in getting properties file: " + e.getMessage());
        }
    }
}
