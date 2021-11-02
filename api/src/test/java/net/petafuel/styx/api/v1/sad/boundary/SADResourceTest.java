package net.petafuel.styx.api.v1.sad.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.sad.entity.ASPSPResponse;
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
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SADResourceTest extends StyxRESTTest {
    private static final String BIC = "HYVEDEMM488";
    private static final String WRONG_BIC = "1337";

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(SADResource.class);
        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void GetAspspDataSuccessTest() throws IOException {
        Jsonb jsonb = JsonbBuilder.create();
        Invocation.Builder invocationBuilder = target("/v1/aspsp/" + BIC).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("Content-Type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());

        ASPSPResponse aspspResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), ASPSPResponse.class);
        Assertions.assertNotNull(aspspResponse);
        Assertions.assertEquals("UniCredit HypoVereinsbank", aspspResponse.getName());
        Assertions.assertNotNull(aspspResponse.getScaApproaches());
        Assertions.assertNotNull(aspspResponse.getSupportedServices());

        Response response2 = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response2.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void GetAspspDataWrongBicTest() {
        Invocation.Builder invocationBuilder = target("/v1/aspsp/" + WRONG_BIC).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("Content-Type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);

        Assertions.assertEquals(404, response.getStatus());
        String errorMessage = response.readEntity(String.class);
        Assertions.assertEquals("{\"message\": \"Aspsp not found for BIC " + WRONG_BIC + " in SAD\"}", errorMessage);
    }
}
