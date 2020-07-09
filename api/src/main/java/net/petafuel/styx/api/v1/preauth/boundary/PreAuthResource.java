package net.petafuel.styx.api.v1.preauth.boundary;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.v1.preauth.entity.PreAuthResponse;
import net.petafuel.styx.core.banklookup.sad.entities.Url;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class PreAuthResource extends RestResource {
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
        PreAuthResponse response = new PreAuthResponse(state, links);

        return Response.status(ResponseConstant.OK).entity(response).build();
    }
}
