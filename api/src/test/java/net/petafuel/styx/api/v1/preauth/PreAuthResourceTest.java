package net.petafuel.styx.api.v1.preauth;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.v1.preauth.boundary.PreAuthResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.UUID;


public class PreAuthResourceTest extends StyxRESTTest {
    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(PreAuthResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchNonExistingPreAuth() {
        Invocation.Builder invocationBuilder = target("/v1/preauth/" + UUID.randomUUID().toString()).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("content-type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(404, response.getStatus());
        ResponseEntity responseEntity = response.readEntity(ResponseEntity.class);
        Assertions.assertEquals(ResponseConstant.STYX_PREAUTH_NOT_FOUND, responseEntity.getCode());
        Assertions.assertEquals(ResponseOrigin.CLIENT, responseEntity.getOrigin());
        Assertions.assertEquals(ResponseCategory.ERROR, responseEntity.getCategory());
    }
}
