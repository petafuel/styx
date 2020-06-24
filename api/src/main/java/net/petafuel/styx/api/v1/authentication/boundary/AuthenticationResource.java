package net.petafuel.styx.api.v1.authentication.boundary;

import net.petafuel.styx.api.filter.AbstractTokenFilter;
import net.petafuel.styx.api.filter.CheckMasterToken;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.api.v1.authentication.control.AuthenticationHandler;
import net.petafuel.styx.api.v1.authentication.entity.ClientTokenInfo;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON})
@CheckMasterToken
public class AuthenticationResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(AuthenticationResource.class);

    /**
     * Creates a new access/client token that is bound to a service and can only be created with a valid master token
     *
     * @param clientTokenInfo mastertoken and service is required for access token creation, expiresIn is optional(default 300)
     * @return returns an access token without metadata
     */
    @POST
    @Path("/auth")
    public Response createClientToken(@Valid @BeanParam ClientTokenInfo clientTokenInfo) {
        if (clientTokenInfo.getExpiresIn() == null) {
            clientTokenInfo.setExpiresIn(Integer.valueOf(System.getProperty(ApiProperties.STYX_ACCESS_TOKEN_UNUSED_LIFETIME, "300")));
        }
        String masterTokenHash = (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName());
        JsonObject response = AuthenticationHandler.createAccessToken(masterTokenHash, clientTokenInfo.getService(), clientTokenInfo.getExpiresIn());

        LOG.info("Created styx accesstoken. master={} unusedLifetime={} serviceBinding={}", PersistentClientApp.get(masterTokenHash).getName(), clientTokenInfo.getExpiresIn(), clientTokenInfo.getService());
        return Response.status(200).entity(response.toString()).build();
    }

}
