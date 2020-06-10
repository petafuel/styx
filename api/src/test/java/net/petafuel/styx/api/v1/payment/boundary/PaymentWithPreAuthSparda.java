package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.preauth.boundary.PreAuthResource;
import net.petafuel.styx.api.v1.preauth.entity.PreAuthResponse;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.TestInstance;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithPreAuthSparda extends StyxRESTTest {
    private static final String BIC = "GENODEF1S04";

    private String currentDate;

    @Override
    protected Application configure() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // your date (java.util.Date)
        cal.add(Calendar.DATE, 1); // You can -/+ x months
        currentDate = simpleDateFormat.format(cal.getTime());
        ResourceConfig config = setupFiltersAndErrorHandlers();

        return config.register(PaymentInitiationResource.class).register(PreAuthResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchPreStepUrl() {
        Invocation.Builder invocationBuilder = target("/v1/preauth").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("scope", "pis");
        Invocation invocation = invocationBuilder.buildPost(null);
        Response response = invocation.invoke(Response.class);
        PreAuthResponse preAuthResponse = response.readEntity(PreAuthResponse.class);
        Assert.assertEquals(200, response.getStatus());
        System.out.println(preAuthResponse.getLinks().getAuthorizationEndpoint().getUrl());
    }
}
