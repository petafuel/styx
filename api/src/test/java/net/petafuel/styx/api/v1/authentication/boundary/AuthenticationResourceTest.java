package net.petafuel.styx.api.v1.authentication.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.v1.authentication.control.TokenGenerator;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * master token restrictions have to be set correctly so that these tests can be successful
 * => keys aispis and ais are used within these tests
 * => restrictions can be f.e. "{ "ais": { "max-usages": 10 }, "aispis": { "max-usages": 5 } }"
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationResourceTest extends StyxRESTTest {
    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        if (masterToken == null || Objects.equals(masterToken, "")) {
            Assertions.fail("test.token.master not set in test properties");
        }
        return config.register(AuthenticationResource.class).register(PaymentInitiationResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchAccessToken() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", masterToken);
        invocationBuilder.header("service", "aispis");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        JsonObject responseEntity = response.readEntity(JsonObject.class);

        Assertions.assertNotNull(responseEntity.getString("token"));
        Assertions.assertEquals(64, responseEntity.getString("token").length());
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchAccessTokenInvalidServiceBinding() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", masterToken);
        invocationBuilder.header("service", "not_implemented");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(400, response.getStatus());
        JsonObject responseEntity = response.readEntity(JsonObject.class);

        Assertions.assertEquals(ResponseCategory.ERROR.name(), responseEntity.getString("category"));
        Assertions.assertEquals(ResponseConstant.BAD_REQUEST.name(), responseEntity.getString("code"));
        Assertions.assertEquals(ResponseOrigin.CLIENT.name(), responseEntity.getString("origin"));
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchAccessTokenInvalidMasterToken() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", TokenGenerator.generateRandomBytes());
        invocationBuilder.header("service", "ais");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(401, response.getStatus());
        JsonObject responseEntity = response.readEntity(JsonObject.class);

        Assertions.assertEquals(ResponseCategory.ERROR.name(), responseEntity.getString("category"));
        Assertions.assertEquals(ResponseConstant.STYX_TOKEN_EXPIRED_OR_REVOKED.name(), responseEntity.getString("code"));
        Assertions.assertEquals(ResponseOrigin.CLIENT.name(), responseEntity.getString("origin"));
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchAccessTokenExpiredBecauseUnused() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", TokenGenerator.generateRandomBytes());
        invocationBuilder.header("service", "ais");
        invocationBuilder.header("expiresIn", "0");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(401, response.getStatus());
        JsonObject responseEntity = response.readEntity(JsonObject.class);

        Assertions.assertEquals(ResponseCategory.ERROR.name(), responseEntity.getString("category"));
        Assertions.assertEquals(ResponseConstant.STYX_TOKEN_EXPIRED_OR_REVOKED.name(), responseEntity.getString("code"));
        Assertions.assertEquals(ResponseOrigin.CLIENT.name(), responseEntity.getString("origin"));
    }

    @Test
    @Category(IntegrationTest.class)
    public void useAccessTokenWithInvalidServiceBinding() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", masterToken);
        invocationBuilder.header("service", "ais");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        JsonObject tokenObject = response.readEntity(JsonObject.class);

        invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", tokenObject.getString("token"));

        invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        response = invocation.invoke(Response.class);
        Assertions.assertEquals(409, response.getStatus());
        JsonObject errorObject = response.readEntity(JsonObject.class);

        Assertions.assertEquals(ResponseCategory.ERROR.name(), errorObject.getString("category"));
        Assertions.assertEquals(ResponseConstant.STYX_TOKEN_ACCESS_VIOLATION.name(), errorObject.getString("code"));
        Assertions.assertEquals(ResponseOrigin.CLIENT.name(), errorObject.getString("origin"));
    }
}
