package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.v1.authentication.control.TokenGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.security.NoSuchAlgorithmException;

@Priority(Priorities.AUTHORIZATION)
public abstract class AbstractTokenFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(AbstractTokenFilter.class);

    public abstract boolean checkToken(String tokenHash);

    @Override
    public void filter(ContainerRequestContext context) {
        String token = context.getHeaderString("token");
        if (token == null || "".equals(token)) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_MISSING_CLIENT_TOKEN, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
        //token is hashed, constant length of 64 characters
        if (token.length() != 64) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_INVALID_TOKEN_FORMAT, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }

        String tokenHash;
        try {
            tokenHash = TokenGenerator.hashSHA256(token);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("plainToken could not be hashed error={}", e.getMessage());
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_INVALID_TOKEN_FORMAT, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }

        boolean tokenValid = checkToken(tokenHash);
        if (!tokenValid) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_TOKEN_EXPIRED_OR_REVOKED, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
        context.setProperty(AbstractTokenFilter.class.getName(), tokenHash);
    }
}
