package net.petafuel.styx.api.filters;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@CheckMasterToken
@Priority(Priorities.AUTHORIZATION)
public class MasterTokenFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {

        JsonObject errorResponse = new JsonObject();
        try {
            String token = containerRequestContext.getHeaderString("token");
            UUID uuid = UUID.fromString(token);
            boolean tokenValid = new PersistentClientApp().get(uuid).isEnabled();
            if (!tokenValid) {
                JsonPrimitive message = new JsonPrimitive("Invalid master token");
                errorResponse.add("message", message);
                containerRequestContext.abortWith(Response.status(401).entity(errorResponse.toString()).build());
            }
        } catch (Exception e) {
            JsonPrimitive message = new JsonPrimitive("A valid UUID 'token' must be submitted as a header.");
            errorResponse.add("message", message);
            containerRequestContext.abortWith(Response.status(401).entity(errorResponse.toString()).build());
        }
    }
}
