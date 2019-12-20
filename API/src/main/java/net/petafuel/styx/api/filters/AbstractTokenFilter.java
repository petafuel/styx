package net.petafuel.styx.api.filters;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.util.UUID;

public abstract class AbstractTokenFilter implements ContainerRequestFilter {

    public abstract boolean checkToken(UUID uuid);

    @Override
    public void filter(ContainerRequestContext context) {

        JsonObject errorResponse = new JsonObject();
        try {
            String token = context.getHeaderString("token");
            UUID uuid = UUID.fromString(token);
            boolean tokenValid = checkToken(uuid);
            if (!tokenValid) {
                JsonPrimitive message = new JsonPrimitive("Invalid master token");
                errorResponse.add("message", message);
                context.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(errorResponse.toString()).build());
            }
        } catch (Exception e) {
            JsonPrimitive message = new JsonPrimitive("A valid UUID 'token' must be submitted as a header.");
            errorResponse.add("message", message);
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(errorResponse.toString()).build());
        }
    }
}
