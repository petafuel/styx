package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import net.petafuel.styx.api.v1.payment.boundary.PaymentStatusResource;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GetConsentResourceFiduciaTest extends GetConsentResourceTest {

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(GetConsentResource.class);
    }

    @Override
    protected String getBIC() {
        return "GENODEF1M03";
    }

    @Override
    public void A_createConsentTest() {

        Invocation invocation = target("/v1/consents").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Override
    public void B_fetchConsentTest() {
        // TODO
    }

    @Override
    public void C_getConsentStatusTest() {
        // TODO
    }
}