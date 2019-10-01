package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.v1.callback.control.CallbackHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource extends Application {

    private CallbackHandler handler = new CallbackHandler();

    @GET
    @Path("/callbacks/{x-request-id}")
    public Response processCallback(@PathParam("x-request-id") String xRequestId) {
        handler.handle(xRequestId);
        return Response.status(200).build();
    }
}
