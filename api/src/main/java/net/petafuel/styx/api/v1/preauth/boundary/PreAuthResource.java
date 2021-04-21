package net.petafuel.styx.api.v1.preauth.boundary;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.authentication.boundary.CheckAccessToken;
import net.petafuel.styx.api.filter.input.boundary.RequiresBIC;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.v1.preauth.entity.GetPreStepResponse;
import net.petafuel.styx.api.v1.preauth.entity.PreAuthResponse;
import net.petafuel.styx.core.banklookup.sad.entities.Url;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;


@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class PreAuthResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(PreAuthResource.class);

    /**
     * Starts an OAuth sessions and returns the preauthId and the link to the authorization page
     * Relevant only for ASPSPs which support/require a preauth in order to access their XS2A interface
     *
     * @return 200 if successful
     */
    @RequiresBIC
    @CheckAccessToken(allowedServices = {XS2ATokenType.AIS, XS2ATokenType.PIS, XS2ATokenType.AISPIS, XS2ATokenType.PIIS})
    @POST
    @Path("/preauth")
    public Response preAuthenticate(@NotEmpty @NotBlank @HeaderParam("scope") String scope) {

        Url url;
        if (Boolean.TRUE.equals(WebServer.isSandbox())) {
            url = getXS2AStandard().getAspsp().getSandboxUrl();
        } else {
            url = getXS2AStandard().getAspsp().getProductionUrl();
        }

        OAuthSession oAuthSession = OAuthService.startPreAuthSession(url, scope);

        String state = oAuthSession.getState();
        String link = OAuthService.buildLink(state, getXS2AStandard().getAspsp().getBic());
        Links links = new Links();
        links.setAuthorizationEndpoint(new Links.Href(link, LinkType.AUTHORIZATION_ENDPOINT));
        PreAuthResponse response = new PreAuthResponse(oAuthSession.getId().toString(), links);

        LOG.info("Successfully started pre-step Authentication within OAuthSession state={}", state);
        return Response.status(ResponseConstant.OK).entity(response).build();
    }

    /**
     * @param preauthId this id should match a state column within the oauth_sessions table
     * @return a GetPreStepResponse Object which contains certain values from an OAuthSession object
     */
    @GET
    @CheckAccessToken(allowedServices = {XS2ATokenType.AIS, XS2ATokenType.PIS, XS2ATokenType.AISPIS, XS2ATokenType.PIIS})
    @Path("/preauth/{preauthId}")
    public Response getPreStepAuthentication(@NotBlank @PathParam("preauthId") String preauthId) {
        OAuthSession oAuthSession;
        try {
            oAuthSession = PersistentOAuthSession.getById(UUID.fromString(preauthId));
        } catch (PersistenceEmptyResultSetException unknownPreauth) {
            throw new StyxException(new ResponseEntity(ResponseConstant.STYX_PREAUTH_NOT_FOUND, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        LOG.info("Successfully retrieved preauth from oauth_session state={}", oAuthSession.getState());
        return Response.status(ResponseConstant.OK).entity(new GetPreStepResponse(oAuthSession)).build();
    }
}
