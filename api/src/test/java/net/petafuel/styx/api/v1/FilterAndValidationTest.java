package net.petafuel.styx.api.v1;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FilterAndValidationTest extends StyxRESTTest {

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(PaymentInitiationResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void APITokenInvalidFormatUUIDv4() throws IOException {

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "abc");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity("test", MediaType.APPLICATION_JSON));
        Response response = invocation.invoke();
        Assertions.assertEquals(400, response.getStatus());
        String strResponse = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertTrue(strResponse.contains(ResponseConstant.STYX_INVALID_TOKEN_FORMAT.name()));
    }

    @Test
    @Category(IntegrationTest.class)
    public void revokedOrExpiredAPIToken() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "expiredexpiredexpiredexpiredexpiredexpiredexpiredexpiredexpirede");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity("test", MediaType.APPLICATION_JSON));
        Response response = invocation.invoke();
        Assertions.assertEquals(401, response.getStatus());
        String strResponse = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertTrue(strResponse.contains(ResponseConstant.UNAUTHORIZED.name()));
    }

    @Test
    @Category(IntegrationTest.class)
    public void missingAPIToken() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity("test", MediaType.APPLICATION_JSON));
        Response response = invocation.invoke();
        Assertions.assertEquals(400, response.getStatus());
        String strResponse = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertTrue(strResponse.contains(ResponseConstant.STYX_MISSING_CLIENT_TOKEN.name()));
    }

    @Test
    @Category(IntegrationTest.class)
    public void missingBIC() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity("test", MediaType.APPLICATION_JSON));
        Response response = invocation.invoke();
        Assertions.assertEquals(400, response.getStatus());
        String strResponse = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
        Assertions.assertTrue(strResponse.contains("PSU-BIC is not provided"));
    }
}
