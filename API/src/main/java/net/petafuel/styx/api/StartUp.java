package net.petafuel.styx.api;

import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartUp {

    private static final Logger LOG = LogManager.getLogger(StartUp.class);

    public static void main(String[] args) {
        PropertyReader propertyReader = new PropertyReader();
        propertyReader.loadProperties();

        WebServer server = new WebServer();
        ThreadManager.getInstance().start();
        try {
            server.startHttpServer();
        } catch (Exception e) {
            LOG.error("Server could not be started message={} stacktrace={}", e.getMessage(), e.getStackTrace());
            server.stopHttpServer();
        }
    }
}
