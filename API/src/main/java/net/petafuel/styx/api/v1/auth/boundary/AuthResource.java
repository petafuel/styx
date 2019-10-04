package net.petafuel.styx.api.v1.auth.boundary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class AuthResource extends Application {

    private static final Logger LOG = LogManager.getLogger(AuthResource.class);

    @POST
    @Path("/auth")
    public Response processAuth() {
        String message = "Auth Called";
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }
}
