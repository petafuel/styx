package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.v1.callback.control.CallbackHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource extends Application {

    private CallbackHandler handler = new CallbackHandler();

    @GET
    @Path("/callbacks/{x-request-id}")
    public Response processCallback(@Context HttpHeaders httpHeaders,
                                    @PathParam("x-request-id") String xRequestId,
                                    @QueryParam("code") String code,
                                    @QueryParam("state") String state,
                                    @QueryParam("error") String error,
                                    @QueryParam("error_description") String errorMessage,
                                    String body) {

        if (state == null) {
            return  handler.handleRedirect(xRequestId, httpHeaders, body);
        }
        return handler.handleOAuth2(code, state, error, errorMessage);
    }
}
