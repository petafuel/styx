package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.persistence.models.ClientApp;
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
            ClientApp clientApp = PersistentClientApp.get(accessToken.getClientMasterToken());
            LOG.warn("Access token expired before first usage, invalidated. master={}, access_token_created={}, serviceBinding={}", clientApp.getName(), accessToken.getCreatedAt(), accessToken.getService());
            PersistentAccessToken.setValid(tokenHash, false);
            return false;
        }
        //Only update last used on if an actual usage would've been possible
        PersistentAccessToken.updateLastUsedOn(tokenHash);
        //get service requirements from Target-Resource class or method
        List<AccessToken.ServiceType> serviceRequirements = null;
        if (ri.getResourceMethod().getAnnotation(CheckAccessToken.class) != null) {
            serviceRequirements = Arrays.asList(ri.getResourceMethod().getAnnotation(CheckAccessToken.class).allowedServices());
        } else if (ri.getResourceClass().getAnnotation(CheckAccessToken.class) != null) {
            serviceRequirements = Arrays.asList(ri.getResourceClass().getAnnotation(CheckAccessToken.class).allowedServices());
        }

        if (serviceRequirements != null && !serviceRequirements.contains(accessToken.getService())) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_TOKEN_ACCESS_VIOLATION, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }

        return accessToken.isValid() && PersistentClientApp.get(accessToken.getClientMasterToken()).isEnabled();
    }
}
