package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.exceptions.CertificateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger LOG = LogManager.getLogger(Config.class);

    public Properties getProperties() {
        return config;
    }

    private Properties config;
    private static Config singletonInstance;

    public static Config getInstance() {
        if (Config.singletonInstance == null) {
            Config.singletonInstance = new Config();
        }
        return Config.singletonInstance;
    }

    private Config() {
        config = new Properties();
        try (
            InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(in);
        } catch (
                IOException e) {
            LOG.error("Error while loading properties: " + e.getMessage());
            throw new CertificateException("Error while loading certificate properties: " + e.getMessage());
        }
    }
}
