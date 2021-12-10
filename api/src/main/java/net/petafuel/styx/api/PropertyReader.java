package net.petafuel.styx.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyReader {

    private static final String PROP_FILENAME = "api.properties";
    private static final Logger LOG = LogManager.getLogger(PropertyReader.class);

    public void loadProperties() {
        //Read from same dir as *.jar before using internal resource
        final Properties props = new Properties();
        InputStream stream;
        try {
            if (Paths.get(PROP_FILENAME).toFile().exists()) {
                stream = new FileInputStream(PROP_FILENAME);
            } else {
                stream = PropertyReader.class.getClassLoader().getResourceAsStream(PROP_FILENAME);
            }
            props.load(stream); // loads all properties of the api.properties - file
        } catch (FileNotFoundException e) {
            LOG.error(String.format("Properties file not found: %s", e.getMessage()));
        } catch (IOException e) {
            LOG.error(String.format("Exception in getting properties file: %s", e.getMessage()));
        }
        System.getProperties().putAll(props);

        LOG.info("Loaded successfully");
    }
}
