package net.petafuel.styx.api.filter.authentication.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.authentication.boundary.CheckAccessToken;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.persistence.models.MasterToken;
import net.petafuel.styx.spi.tokentypemapper.TokenTypeMapperService;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import net.petafuel.styx.spi.tokentypemapper.spi.TokenTypeMapperSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Provider
@CheckAccessToken
@Priority(Priorities.AUTHORIZATION)
public class AccessTokenFilter extends AbstractTokenFilter {
    private static final Logger LOG = LogManager.getLogger(AccessTokenFilter.class);

    @Context
    ResourceInfo ri;

    @Override
    public boolean checkToken(String tokenHash) {
        AccessToken accessToken;
        try {
            accessToken = PersistentAccessToken.get(tokenHash);
        } catch (PersistenceEmptyResultSetException persistenceEmptyResultSetException) {
            //if there was no matching token found in the database, always return unauthorized
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.UNAUTHORIZED, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }

        if (accessToken.getLastUsedOn() == null && (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - accessToken.getCreatedAt().getTime())) > accessToken.getExpiresIn()) {
            MasterToken masterToken = PersistentClientApp.get(accessToken.getClientMasterToken());
            LOG.warn("Access token expired before first usage, invalidated. master={}, access_token_created={}, serviceBinding={}", masterToken.getName(), accessToken.getCreatedAt(), accessToken.getServiceType());
            PersistentAccessToken.setValid(tokenHash, false);
            return false;
        }

        //get master token and check restrictions
        MasterToken masterToken = PersistentClientApp.get(accessToken.getClientMasterToken());
        checkRestrictions(masterToken, accessToken.getServiceType());
        //check if maxUsages is reached
        checkMaxUsages(masterToken, accessToken);
        //log necessary token information
        LOG.info("Request sent with following token information: accessToken={} valid={} serviceType={} usages={} clientReference={} createdAt={} masterTokenName={} masterTokenEnabled={}",
                accessToken.getId(), accessToken.isValid(), accessToken.getServiceType(), accessToken.getUsages(), accessToken.getClientReference(), accessToken.getCreatedAt(), masterToken.getName(), masterToken.isEnabled());

        //get service requirements from Target-Resource class or method
        List<XS2ATokenType> serviceRequirements = null;
        if (ri.getResourceMethod().getAnnotation(CheckAccessToken.class) != null) {
            serviceRequirements = Arrays.asList(ri.getResourceMethod().getAnnotation(CheckAccessToken.class).allowedServices());
        } else if (ri.getResourceClass().getAnnotation(CheckAccessToken.class) != null) {
            serviceRequirements = Arrays.asList(ri.getResourceClass().getAnnotation(CheckAccessToken.class).allowedServices());
        }

        //Get all TokenTypeMapperSPI implementations
        List<TokenTypeMapperSPI> tokenTypeMapperImpls = new TokenTypeMapperService().providers();
        TokenTypeMapperSPI concreteTokenTypeMapper = tokenTypeMapperImpls
                .stream()
                .filter(tokenTypeMapperSPI -> tokenTypeMapperSPI.getMapping(accessToken.getServiceType()) != null)
                .findFirst()
                .orElse(null);

        if (concreteTokenTypeMapper == null || (serviceRequirements != null && !serviceRequirements.contains(concreteTokenTypeMapper.getMapping(accessToken.getServiceType())))) {
            if (concreteTokenTypeMapper == null) {
                LOG.error("There was not TokenTypeMapperSPI implementation found within the classpath, tokens cannot be validated against access controll");
            }
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_TOKEN_ACCESS_VIOLATION, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }

        //update lastUsedOn and increase usages of accessToken
        PersistentAccessToken.updateLastUsedOn(tokenHash);

        return accessToken.isValid() && masterToken.isEnabled();
    }

    /**
     * Check if master token Restrictions restrict usages of accessToken
     * @param masterToken model of MasterToken
     * @param serviceType string serviceType of accessToken
     */
    public void checkRestrictions(MasterToken masterToken, String serviceType) {
        if (masterToken.getRestrictions() == null || !masterToken.getRestrictions().get(serviceType).isValid()) {
            LOG.error("Master Token restrictions not valid: masterTokenName={}", masterToken.getName());
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED, ResponseCategory.ERROR, ResponseOrigin.STYX);
            throw new StyxException(responseEntity);
        }
    }

    /**
     * Check if maximum amount of usages reached => exception if reached, increase usage of token if not reached yet
     *
     * @param masterToken model of MasterToken
     * @param accessToken model of AccessToken
     */
    public void checkMaxUsages(MasterToken masterToken, AccessToken accessToken) {
        int maxUsages = masterToken.getRestrictions().get(accessToken.getServiceType()).getMaxUsages();
        if (maxUsages > 0 && maxUsages <= accessToken.getUsages()) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_TOKEN_ACCESS_EXEEDED, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
    }
}
