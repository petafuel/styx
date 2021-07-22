package net.petafuel.styx.api;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.glassfish.jersey.test.spi.TestHelper;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StyxTestContainerFactory implements TestContainerFactory {
    public StyxTestContainerFactory() {
    }

    public TestContainer create(URI baseUri, DeploymentContext context) throws IllegalArgumentException {
        return new StyxTestContainerFactory.StyxTestContainer(baseUri, context);
    }

    private static class StyxTestContainer implements TestContainer {
        private static final Logger LOGGER = Logger.getLogger(StyxTestContainer.class.getName());
        private final Server server;
        private URI baseUri;

        private StyxTestContainer(URI baseUri, DeploymentContext context) {
            URI base = UriBuilder.fromUri(baseUri).path(context.getContextPath()).build();
            if (!"/".equals(base.getRawPath())) {
                throw new TestContainerException(String.format("Cannot deploy on %s. Jetty HTTP container only supports deployment on root path.", base.getRawPath()));
            } else {
                this.baseUri = base;
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Creating JettyTestContainer configured at the base URI " + TestHelper.zeroPortToAvailablePort(baseUri));
                }

                ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
                servletContext.setContextPath("/");
                this.server = new Server(this.baseUri.getPort());
                this.server.setHandler(servletContext);
                ServletHolder styxTestRoutes = new ServletHolder(new ServletContainer(context.getResourceConfig()));
                servletContext.addServlet(styxTestRoutes, "/*");
            }
        }

        public ClientConfig getClientConfig() {
            return null;
        }

        public URI getBaseUri() {
            return this.baseUri;
        }

        public void start() {
            if (this.server.isStarted()) {
                LOGGER.log(Level.WARNING, "Ignoring start request - JettyTestContainer is already started.");
            } else {
                LOGGER.log(Level.FINE, "Starting JettyTestContainer...");

                try {
                    this.server.start();
                    if (this.baseUri.getPort() == 0) {
                        int port = 0;
                        Connector[] var2 = this.server.getConnectors();
                        int var3 = var2.length;

                        for (int var4 = 0; var4 < var3; ++var4) {
                            Connector connector = var2[var4];
                            if (connector instanceof ServerConnector) {
                                port = ((ServerConnector) connector).getLocalPort();
                                break;
                            }
                        }

                        this.baseUri = UriBuilder.fromUri(this.baseUri).port(port).build();
                        LOGGER.log(Level.INFO, "Started JettyTestContainer at the base URI " + this.baseUri);
                    }
                } catch (Exception var6) {
                    throw new TestContainerException(var6);
                }
            }

        }

        public void stop() {
            if (this.server.isStarted()) {
                LOGGER.log(Level.FINE, "Stopping JettyTestContainer...");

                try {
                    this.server.stop();
                } catch (Exception var2) {
                    LOGGER.log(Level.WARNING, "Error Stopping JettyTestContainer...", var2);
                }
            } else {
                LOGGER.log(Level.WARNING, "Ignoring stop request - JettyTestContainer is already stopped.");
            }

        }
    }
}