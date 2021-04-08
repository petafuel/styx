package net.petafuel.styx.api;

import net.petafuel.styx.api.event.AccessTraceAdapter;
import net.petafuel.styx.api.event.RequestUUIDAdapter;
import net.petafuel.styx.api.exception.BankRequestFailedExceptionHandler;
import net.petafuel.styx.api.exception.ClientExceptionHandler;
import net.petafuel.styx.api.exception.ConstraintViolationExceptionHandler;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxExceptionHandler;
import net.petafuel.styx.api.exception.UncaughtExceptionHandler;
import net.petafuel.styx.api.filter.authentication.control.AccessTokenFilter;
import net.petafuel.styx.api.filter.authentication.control.MasterTokenFilter;
import net.petafuel.styx.api.filter.authentication.control.PreAuthAccessFilter;
import net.petafuel.styx.api.filter.input.control.BICFilter;
import net.petafuel.styx.api.filter.input.control.MandatoryHeaderFilter;
import net.petafuel.styx.api.filter.input.control.PSUFilter;
import net.petafuel.styx.api.filter.input.control.SADInitialisationFilter;
import net.petafuel.styx.api.filter.input.control.SandboxHeaderPassthroughs;
import net.petafuel.styx.api.filter.output.control.ReferenceHeaderFilter;
import net.petafuel.styx.api.injection.ServiceBinder;
import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.api.v1.account.boundary.AccountResource;
import net.petafuel.styx.api.v1.authentication.boundary.AuthenticationResource;
import net.petafuel.styx.api.v1.callback.boundary.CallbackResource;
import net.petafuel.styx.api.v1.consent.boundary.ConsentAuthorisationResource;
import net.petafuel.styx.api.v1.consent.boundary.CreateConsentResource;
import net.petafuel.styx.api.v1.consent.boundary.GetConsentResource;
import net.petafuel.styx.api.v1.payment.boundary.FetchPaymentResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentAuthorisationResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentStatusResource;
import net.petafuel.styx.api.v1.preauth.boundary.PreAuthResource;
import net.petafuel.styx.api.v1.status.boundary.StatusResource;
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
    private static final Boolean SANDBOX_ENABLED = Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_API_SAD_SANDBOX_ENABLED, "true"));
    private final String ip;
    private final String port;
    private Server server = null;

    public WebServer() {
        this.ip = System.getProperty(ApiProperties.STYX_API_IP);
        this.port = System.getProperty(ApiProperties.STYX_API_PORT);
    }

    public static Boolean isSandbox() {
        return SANDBOX_ENABLED;
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
                .register(AuthenticationResource.class)                 // handle Styx API Authentication for access and master tokens
                .register(GetConsentResource.class)                     // handle consent fetching
                .register(CreateConsentResource.class)                  // handle consent creation
                .register(ConsentAuthorisationResource.class)           // handle consent authorisation
                .register(PaymentStatusResource.class)                  // handle payment status
                .register(PaymentInitiationResource.class)              // handle payment initiation calls
                .register(FetchPaymentResource.class)                   // handle fetch payment calls
                .register(PaymentAuthorisationResource.class)           // handle payment SCA calls
                .register(PreAuthResource.class)
                .register(StatusResource.class);                        //Handle Status calls from internal redirects, e.g. in SCA cases after a callback was received
        //Register Middlewares / Filters
        config.register(AccessTokenFilter.class)                        // request Requires valid client token and enabled master token
                .register(PSUFilter.class)                              // request requires PSU data
                .register(BICFilter.class)                              // request requires PSU data
                .register(MandatoryHeaderFilter.class)                  // request requires certain header fields
                .register(MasterTokenFilter.class)                      // request requires enabled master token
                .register(SADInitialisationFilter.class)                // dynamically initialize Services from SAD
                .register(PreAuthAccessFilter.class)                    // make preauth access token available in REST Endpoints
                .register(ReferenceHeaderFilter.class);                 // add Reference header to all REST responses

        if (Boolean.TRUE.equals(WebServer.isSandbox())) {
            config.register(SandboxHeaderPassthroughs.class);            // makes all X-STYX-... headers available in the request context, if styx is running in sandbox mode
        }

        //Register Errorhandlers
        config.register(UncaughtExceptionHandler.class)                 // handle any uncaught exceptions
                .register(BankRequestFailedExceptionHandler.class)      // handle xs2a interface exception
                .register(StyxExceptionHandler.class)                   // handle styx exceptions
                .register(ClientExceptionHandler.class)                 // handle 4xx client exceptions
                .register(ConstraintViolationExceptionHandler.class);   // handle validation exceptions

        config.register(new ServiceBinder());                           // bind service classes for CDI

        ServletHolder styxRoutes = new ServletHolder(new ServletContainer(config));
        context.addServlet(styxRoutes, "/*");
        context.addEventListener(new RequestUUIDAdapter());             // add uuid to every log entry served for one single request
        context.addEventListener(new AccessTraceAdapter());             // Log incoming request and outgoing response meta data(no http body) on the Styx REST interface

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
            LOG.error("An out of context error happened baseURI={} status={}", baseRequest.getRequestURI(), response.getStatus());
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
