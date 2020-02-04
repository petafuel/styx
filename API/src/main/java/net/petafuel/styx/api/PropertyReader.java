package net.petafuel.styx.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private static final String PROP_FILENAME = "api.properties";
    private static final Logger LOG = LogManager.getLogger(PropertyReader.class);

    public void loadProperties() {

        try {
            final Properties props = new Properties();
            final File propertiesFile = new File(PROP_FILENAME);


            LOG.info("Loading '{}' from resources directory: {}", PROP_FILENAME, propertiesFile.getAbsolutePath());
            try (FileInputStream stream = new FileInputStream("./" + PROP_FILENAME)) {
                props.load(stream); // loads all properties of the config.properties - file
            } catch (FileNotFoundException e) {
                LOG.error("Exception in getting properties file: " + e.getMessage());
                throw new FileNotFoundException("Config-File was not found in directory");
            }
            System.getProperties().putAll(props);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load config file: " + PROP_FILENAME);
        }
        LOG.info("Loaded successfully");
    }
}
