package net.petafuel.styx.api;

import net.petafuel.styx.api.v1.account.boundary.AccountResource;
import net.petafuel.styx.api.v1.auth.boundary.AuthResource;
import net.petafuel.styx.api.v1.callback.boundary.CallbackResource;
import com.sun.net.httpserver.HttpServer;
import net.petafuel.styx.api.v1.consent.boundary.ConsentResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;


public class WebServer {

    private static final Logger LOG = LogManager.getLogger(WebServer.class);
    private String schema;
    private String ip;
    private String port;
    private HttpServer server = null;

    public WebServer() {
        this.schema = System.getProperty("web.schema");
        this.ip = System.getProperty("web.ip");
        this.port = System.getProperty("web.port");
    }

    void startHttpServer() throws Exception {
        try {
            ResourceConfig config = new ResourceConfig()
                    .register(CallbackResource.class)
                    .register(AccountResource.class)
                    .register(AuthResource.class)
                    .register(ConsentResource.class)
                    .register(PaymentResource.class);

            try {
                String url = schema + ip + ":" + port + "/";
                LOG.info("Trying to connect to Server");
                server = JdkHttpServerFactory.createHttpServer(URI.create(url), config);
                LOG.info("Connected");
            } catch (Exception e) {
                LOG.error("Unable to connect to Server: " + e);
                throw new Exception("Could not connect to Server");
            }
        } catch (Exception e) {
            LOG.error("Unable to initialize config-file: " + e);
            throw new Exception("Could not initialize config-file");
        }
    }


    void stopHttpServer() {
        if (server != null) {
            server.stop(0);
            LOG.warn("Server has been stopped");
        }
    }
}
