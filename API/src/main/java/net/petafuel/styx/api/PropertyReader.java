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


        final Properties props = new Properties();
        try (InputStream stream = PropertyReader.class.getClassLoader().getResourceAsStream(PROP_FILENAME)) {
            props.load(stream); // loads all properties of the config.properties - file
        } catch (FileNotFoundException e) {
            LOG.error("Properties file not found: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("Exception in getting properties file: " + e.getMessage());
        }
        System.getProperties().putAll(props);

        LOG.info("Loaded successfully");
    }
}
