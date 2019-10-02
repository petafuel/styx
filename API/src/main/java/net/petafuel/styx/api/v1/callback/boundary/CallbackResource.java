package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.v1.callback.control.CallbackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource extends Application {

    private CallbackHandler handler = new CallbackHandler();
    private static final Logger LOG = LogManager.getLogger(CallbackResource.class);

    @GET
    @Path("/callbacks/{x-request-id}")
    public Response processCallback(@Context HttpHeaders httpHeaders,
                                    @PathParam("x-request-id") String xRequestId,
                                    String body) {
        handler.handle(xRequestId);

        StringBuilder output = new StringBuilder();
        for(String field : httpHeaders.getRequestHeaders().keySet()){
            output.append(" ").append(field).append(": ").append(httpHeaders.getRequestHeader(field)).append("\n");
        }
        LOG.info("\n Request Header \n" + output + "\n Request Body \n" + body);
        String message = "Gettings Callbacks with x-request-id: " + xRequestId;
        return Response.status(200).entity(message).build();
    }
}
