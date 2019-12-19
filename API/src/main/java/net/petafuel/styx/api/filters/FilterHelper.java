package net.petafuel.styx.api.filters;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.UUID;

class FilterHelper {

    private FilterHelper(){}

    static void checkToken(ContainerRequestContext context, Object persistent) {

        JsonObject errorResponse = new JsonObject();
        try {
            String token = context.getHeaderString("token");
            UUID uuid = UUID.fromString(token);
            boolean tokenValid = false;
            if (persistent instanceof PersistentClientApp) {
                tokenValid = ((PersistentClientApp) persistent).get(uuid).isEnabled();
            } else if (persistent instanceof PersistentAccessToken) {
                tokenValid = ((PersistentAccessToken) persistent).get(uuid).isValid();
            }
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
