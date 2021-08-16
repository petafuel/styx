package net.petafuel.styx.api.v1.authentication.boundary;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.authentication.boundary.CheckMasterToken;
import net.petafuel.styx.api.filter.authentication.control.AbstractTokenFilter;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.ApiProperties;
import net.petafuel.styx.api.v1.authentication.control.AuthenticationHandler;
import net.petafuel.styx.api.v1.authentication.control.TokenGenerator;
import net.petafuel.styx.api.v1.authentication.entity.ClientTokenInfo;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import net.petafuel.styx.core.persistence.models.MasterToken;
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
import java.security.NoSuchAlgorithmException;

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
        //get master token
        String masterTokenHash = (String) getContainerRequestContext().getProperty(AbstractTokenFilter.class.getName());
        MasterToken masterToken = PersistentClientApp.get(masterTokenHash);

        checkMasterTokenRestrictions(masterToken, clientTokenInfo);

        //hash client reference if given
        String clientReference = clientTokenInfo.getClientReference();
        if (clientReference != null) {
            try {
                clientReference = TokenGenerator.hashSHA256(clientReference);
            } catch (NoSuchAlgorithmException e) {
                ResponseEntity responseEntity = new ResponseEntity(e.getMessage(), ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX);
                throw new StyxException(responseEntity);
            }
        }

        //create access token
        JsonObject response = AuthenticationHandler.createAccessToken(masterTokenHash, clientTokenInfo.getService(), clientTokenInfo.getExpiresIn(), clientReference);
        LOG.info("Created styx accesstoken. master={} unusedLifetime={} serviceBinding={} clientReference={}", masterToken.getName(), clientTokenInfo.getExpiresIn(), clientTokenInfo.getService(), clientReference);
        return Response.status(200).entity(response.toString()).build();
    }

    /**
     * checks if master token allows specific service type and checks if service type restriction is valid
     *
     * @param masterToken     MasterToken
     * @param clientTokenInfo ClientTokenInfo
     */
    private void checkMasterTokenRestrictions(MasterToken masterToken, ClientTokenInfo clientTokenInfo) {
        //check if requested service is supported by master token
        ResponseEntity responseEntity = null;
        if (masterToken.getRestrictions() != null && !masterToken.getRestrictions().containsKey(clientTokenInfo.getService())) {
            responseEntity = new ResponseEntity(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
        } else if (masterToken.getRestrictions() == null || !masterToken.getRestrictions().get(clientTokenInfo.getService()).isValid()) {
            LOG.error("Master Token restrictions not valid: masterTokenName={}", masterToken.getName());
            responseEntity = new ResponseEntity(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED, ResponseCategory.ERROR, ResponseOrigin.STYX);
        }
        if (responseEntity != null) {
            throw new StyxException(responseEntity);
        }
    }
}
