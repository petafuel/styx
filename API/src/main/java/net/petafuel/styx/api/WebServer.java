package net.petafuel.styx.api;

import net.petafuel.styx.api.exception.BankRequestFailedExceptionHandler;
import net.petafuel.styx.api.exception.ClientExceptionHandler;
import net.petafuel.styx.api.exception.ConstraintViolationExceptionHandler;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxExceptionHandler;
import net.petafuel.styx.api.exception.UncaughtExceptionHandler;
import net.petafuel.styx.api.filter.AuthorizedFilter;
import net.petafuel.styx.api.filter.BICFilter;
import net.petafuel.styx.api.filter.MandatoryHeaderFilter;
import net.petafuel.styx.api.filter.MasterTokenFilter;
import net.petafuel.styx.api.filter.PSUFilter;
import net.petafuel.styx.api.injection.ServiceBinder;
import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.api.v1.account.boundary.AccountResource;
import net.petafuel.styx.api.v1.auth.boundary.AuthResource;
import net.petafuel.styx.api.v1.callback.boundary.CallbackResource;
import net.petafuel.styx.api.v1.consent.boundary.ConsentResource;
import net.petafuel.styx.api.v1.payment.boundary.FetchPaymentResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class WebServer {
    private static final Logger LOG = LogManager.getLogger(WebServer.class);
    private static Boolean isSandbox = Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_API_SAD_SANDBOX_ENABLED, "true"));
    private String ip;
    private String port;
    private Server server = null;

    public WebServer() {
        this.ip = System.getProperty(ApiProperties.STYX_API_IP);
        this.port = System.getProperty(ApiProperties.STYX_API_PORT);
    }

    public static Boolean isSandbox() {
        return isSandbox;
    }

    public void startHttpServer() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(ip), Integer.parseInt(port));


        server = new Server(inetSocketAddress);
        server.setHandler(context);
        ResourceConfig config = new ResourceConfig();
        //jetty hardening, handle any out of context errors
        context.setErrorHandler(new ErrorHandler());

        //Register Resources / REST Endpoints
        config.register(CallbackResource.class)
                .register(AccountResource.class)
                .register(AuthResource.class)
                .register(ConsentResource.class)
                .register(PaymentInitiationResource.class)              //Handle payment initiation calls
                .register(FetchPaymentResource.class);                  //Handle fetch payment calls
        //Register Middlewares / Filters
        config.register(AuthorizedFilter.class)                         // request Requires valid client token and enabled master token
                .register(PSUFilter.class)                              // request requires PSU data
                .register(BICFilter.class)                              // request requires PSU data
                .register(MandatoryHeaderFilter.class)                  // request requires certain header fields
                .register(MasterTokenFilter.class);                     // Request requires enabled master token
        //Register Errorhandlers
        config.register(UncaughtExceptionHandler.class)                 // handle any uncaught exceptions
                .register(BankRequestFailedExceptionHandler.class)      // handle xs2a interface exception
                .register(StyxExceptionHandler.class)                   // handle styx exceptions
                .register(ClientExceptionHandler.class)                 // handle 4xx client exceptions
                .register(ConstraintViolationExceptionHandler.class);   // handle validation exceptions

        config.register(new ServiceBinder());

        ServletHolder styxRoutes = new ServletHolder(new ServletContainer(config));
        context.addServlet(styxRoutes, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            LOG.error("Error: {}", ex.getMessage());
        } finally {

            server.destroy();
        }
    }

    public void stopHttpServer() {
        if (server != null) {
            server.destroy();
            LOG.warn("Server has been stopped");
        }
    }

    //ErrorHandler is predefined by jetty
    @SuppressWarnings("squid:MaximumInheritanceDepth")
    static class ErrorHandler extends ErrorPageErrorHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            LOG.fatal("An out of context error happened baseURI={} status={}", baseRequest.getRequestURI(), response.getStatus());
            ResponseEntity responseEntity = new ResponseEntity("Internal Server Error", ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX);
            try (Jsonb jsonb = JsonbBuilder.create()) {
                response.setStatus(ResponseConstant.INTERNAL_SERVER_ERROR.getStatusCode());
                response.setContentType(MediaType.APPLICATION_JSON);
                response.getWriter().append(jsonb.toJson(responseEntity));
            } catch (Exception e) {
                response.setContentType(MediaType.APPLICATION_JSON);
                response.getWriter().append("{\"message\": \"Internal Server error\"}");
            }
        }
    }
}
