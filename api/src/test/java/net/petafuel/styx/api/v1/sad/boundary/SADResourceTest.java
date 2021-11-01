package net.petafuel.styx.api.v1.sad.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

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
    public void GetAspspDataSuccessTest() {
        Invocation.Builder invocationBuilder = target("/v1/aspsp/" + BIC).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("Content-Type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
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
        Assertions.assertEquals("{\"message\": \"Aspsp not found for BIC " + WRONG_BIC + " in SAD\"}", response.getEntity());
    }
}
