package net.petafuel.styx.api.v1.payment.boundary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class PaymentResource extends Application {

    private static final Logger LOG = LogManager.getLogger(PaymentResource.class);

//    Reads the accounts of the available payment account depending on the consent granted.
    @POST
    @Path("/payment/initiate")
    public Response processAuth() {
        String message = "Initate a Payment";
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }
}
