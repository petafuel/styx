package net.petafuel.styx.api.v1.auth.boundary;

import com.google.gson.JsonElement;
import net.petafuel.styx.api.filters.CheckMasterToken;
import net.petafuel.styx.api.v1.auth.control.AuthHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckMasterToken
public class AuthResource extends Application {

    private static final Logger LOG = LogManager.getLogger(AuthResource.class);
    private AuthHandler handler = new AuthHandler();

    @POST
    @Path("/auth")
    public Response processAuth(@Context HttpHeaders httpHeaders) {

        String masterToken = httpHeaders.getRequestHeader("token").get(0);
        JsonElement response = this.handler.createAccessToken(UUID.fromString(masterToken));
        LOG.info("An access_token was created");
        return Response.status(200).entity(response.toString()).build();
    }

}
