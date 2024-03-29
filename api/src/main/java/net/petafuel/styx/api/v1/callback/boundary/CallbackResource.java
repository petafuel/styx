package net.petafuel.styx.api.v1.callback.boundary;

import net.petafuel.styx.api.util.Sanitizer;
import net.petafuel.styx.api.v1.callback.control.CallbackHandler;
import net.petafuel.styx.api.v1.callback.control.OAuthCallbackProcessor;
import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class CallbackResource {

    /**
     * Callback Endpoint for XS2A SCA Flows
     *
     * @param serviceRealm   should be a known REALM
     * @param realmParameter generic string for usage in CallBackHandler&Processor
     * @param styxReference  should be the requestUUID/xrequest id of a previous styx session in payment initiation or consent creation
     * @param oAuthCallback  query parameters for success or error redirects from an aspsp
     * @return will redirect to styx status pages
     */
    @GET
    @Path("/callbacks/{service-realm}/{realm-parameter}/{styx-reference}")
    @Produces(MediaType.TEXT_HTML)
    public Response processCallback(@PathParam("service-realm") String serviceRealm, @PathParam("realm-parameter") String realmParameter, @PathParam("styx-reference") String styxReference,
                                    @BeanParam OAuthCallback oAuthCallback) {
        serviceRealm = Sanitizer.replaceEscSeq(serviceRealm);
        realmParameter = Sanitizer.replaceEscSeq(realmParameter);
        styxReference = Sanitizer.replaceEscSeq(styxReference);

        return CallbackHandler.handleCallback(serviceRealm, realmParameter, styxReference, oAuthCallback);
    }

    /**
     * Legacy Callback if the aspsp is not correctly forwarding the redirect back to styx
     *
     * @param realm should be a known REALM
     * @param param generic string for usage in CallBackHandler&Processor
     * @return returns a redirect to styx status pages
     */
    @GET
    @Path("/callbacks/{realm}/{param}")
    @Produces(MediaType.TEXT_HTML)
    public Response processCallback(@PathParam("realm") String realm, @PathParam("param") String param) {
        realm = Sanitizer.replaceEscSeq(realm);
        param = Sanitizer.replaceEscSeq(param);
        return CallbackHandler.handleCallback(realm, param, null, null);
    }

    /**
     * Receive a callback on an oauth realm
     *
     * @param oAuthCallback query parameters for success or error redirects from an aspsp
     * @return will redirect to styx status pages
     */
    @GET
    @Path("/callbacks/oauth/sca")
    @Produces(MediaType.TEXT_HTML)
    public Response processOAuthCallback(
            @BeanParam OAuthCallback oAuthCallback) {
        return CallbackHandler.handleCallback("OAUTH", "sca", null, oAuthCallback);
    }

    /**
     * Callback we receive if there was a preceding pre-step Authentication(pre-auth)
     *
     * @param statePath    contains a state that is referenced within styx database
     * @param code         oauth query parameter
     * @param stateQuery   state query parameter
     * @param error        error query parameter
     * @param errorMessage errorMessage query parameter
     * @return will redirect to styx status pages
     */
    @GET
    @Path("/callbacks/oauth/preauth/{statePath}")
    @Produces(MediaType.TEXT_HTML)
    public Response processPreStepCallback(
            @PathParam("statePath") String statePath,
            @QueryParam("code") String code,
            @QueryParam("state") String stateQuery,
            @QueryParam("error") String error,
            @QueryParam("error_description") String errorMessage) {
        statePath = Sanitizer.replaceEscSeq(statePath);
        code = Sanitizer.replaceEscSeq(code);
        error = Sanitizer.replaceEscSeq(error);
        errorMessage = Sanitizer.replaceEscSeq(errorMessage);

        return OAuthCallbackProcessor.handlePreStepOAuth2(code, statePath, error, errorMessage, "oauth/preauth/" + statePath);
    }
}
