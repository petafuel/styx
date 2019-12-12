package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.filters.CheckAccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
public class ConsentResource extends Application {

    private static final Logger LOG = LogManager.getLogger(ConsentResource.class);

//    Reads the status of an account information consent resource.
    @GET
    @Path("/consent/status")
    public Response processAccountList() {
        String message= "Getting the Status of the Consent";
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }
}
