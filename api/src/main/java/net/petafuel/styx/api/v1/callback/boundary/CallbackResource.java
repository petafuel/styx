package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.v1.callback.control.CallbackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource {
    private static final Logger LOG = LogManager.getLogger(CallbackResource.class);
    private final CallbackHandler handler = new CallbackHandler();

    @GET
    @Path("/callbacks/{realm}/{param}/{requestuuid}")
    @Produces(MediaType.TEXT_HTML)
    public Response processCallback(@Context HttpHeaders httpHeaders, @PathParam("realm") String realm, @PathParam("param") String param, @PathParam("requestuuid") String requestUUID) {
        LOG.info("Received callback for resource realm={}, param={}, originRequestUUID={}, xForwardedFor={}", realm, param, requestUUID, httpHeaders.getHeaderString("x-forwarded-for"));
        return handler.handleRedirect(realm, param, requestUUID);
    }

    @GET
    @Path("/callbacks/{realm}/{param}")
    @Produces(MediaType.TEXT_HTML)
    public Response processCallback(@PathParam("realm") String realm, @PathParam("param") String param) {
        LOG.info("Received callback for resource realm={}, param={}, resource id missing", realm, param);
        return handler.handleRedirect(realm, param, null);
    }

    @GET
    @Path("/callbacks/oauth/sca")
    @Produces(MediaType.TEXT_HTML)
    public Response processOAuthCallback(
            @Context HttpHeaders httpHeaders,
            @QueryParam("code") String code,
            @QueryParam("state") String state,
            @QueryParam("error") String error,
            @QueryParam("error_description") String errorMessage) {
        return handler.handleOAuth2(code, state, error, errorMessage);
    }

    @GET
    @Path("/callbacks/oauth/preauth/{statePath}")
    @Produces(MediaType.TEXT_HTML)
    public Response processPreStepCallback(
            @Context HttpHeaders httpHeaders,
            @PathParam("statePath") String statePath,
            @QueryParam("code") String code,
            @QueryParam("state") String stateQuery,
            @QueryParam("error") String error,
            @QueryParam("error_description") String errorMessage) {
        return handler.handlePreStepOAuth2(code, statePath, error, errorMessage);
    }
}
