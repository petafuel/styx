package net.petafuel.styx.api;

import net.petafuel.styx.api.exception.BankRequestFailedExceptionHandler;
import net.petafuel.styx.api.exception.ClientExceptionHandler;
import net.petafuel.styx.api.exception.ConstraintViolationExceptionHandler;
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
import net.petafuel.styx.api.injection.ServiceBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;

import java.util.TimeZone;

//This is just a setup class
@SuppressWarnings("squid:S2187")
public class StyxRESTTest extends JerseyTest {
    protected static String aisAccessToken;
    protected static String pisAccessToken;
    protected static String masterToken;
    protected static String targobankToken;

    protected ResourceConfig setupFiltersAndErrorHandlers() {
        new PropertyReader().loadProperties();
        aisAccessToken = System.getProperty("test.token.access.ais");
        pisAccessToken = System.getProperty("test.token.access.pis");
        masterToken = System.getProperty("test.token.master");
        if (aisAccessToken == null || pisAccessToken == null) {
            Assertions.fail("test.token.access.ais or test.token.access.pis is not set");
        }
        targobankToken = System.getProperty("test.token.targobank");

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        ResourceConfig config = new ResourceConfig();
        //Register Middlewares / Filters
        config.register(AccessTokenFilter.class)                         // request Requires valid client token and enabled master token
                .register(PSUFilter.class)                              // request requires PSU data
                .register(BICFilter.class)                              // request requires PSU data
                .register(MandatoryHeaderFilter.class)                  // request requires certain header fields
                .register(SandboxHeaderPassthroughs.class)              // allow X-STYX- headers to be forwarded to the aspsp
                .register(SADInitialisationFilter.class)              // allow X-STYX- headers to be forwarded to the aspsp
                .register(PreAuthAccessFilter.class)              // allow X-STYX- headers to be forwarded to the aspsp
                .register(MasterTokenFilter.class);                     // Request requires enabled master token
        //Register Errorhandlers
        config.register(UncaughtExceptionHandler.class)                 // handle any uncaught exceptions
                .register(BankRequestFailedExceptionHandler.class)      // handle xs2a interface exception
                .register(StyxExceptionHandler.class)                   // handle styx exceptions
                .register(ClientExceptionHandler.class)                 // handle 4xx client exceptions
                .register(ConstraintViolationExceptionHandler.class);   // handle validation exceptions

        config.register(new ServiceBinder());
        return config;
    }
}
