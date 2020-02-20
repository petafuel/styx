package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.v1.callback.control.CallbackHandler;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource extends Application {

    private CallbackHandler handler = new CallbackHandler();

    @GET
    @Path("/callbacks/{x-request-id}")
    @Produces(MediaType.TEXT_HTML)
    public Response processCallback(@Context HttpHeaders httpHeaders,
                                    @PathParam("x-request-id") String xRequestId,
                                    @QueryParam("code") String code,
                                    @QueryParam("state") String state,
                                    @QueryParam("error") String error,
                                    @QueryParam("error_description") String errorMessage) {

        if (state == null) {
            return handler.handleRedirect(xRequestId, httpHeaders, "");
        }
        return handler.handleOAuth2(code, state, error, errorMessage);
    }
}
