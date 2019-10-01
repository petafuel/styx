package net.petafuel.styx.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartUp {

    private static final Logger LOG = LogManager.getLogger(StartUp.class);

    public static void main(String[] args) {
        PropertyReader propertyReader = new PropertyReader();
        propertyReader.loadProperties();

        WebServer server = new WebServer();
        try {
            server.startHttpServer();

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Server could not be started: " + e.getMessage());
            server.stopHttpServer();
        }
    }
}
