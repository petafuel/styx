package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.UUID;

public abstract class AbstractTokenFilter implements ContainerRequestFilter {

    public abstract boolean checkToken(UUID uuid);

    @Override
    public void filter(ContainerRequestContext context) {
        String token = context.getHeaderString("token");
        if (token == null || "".equals(token)) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_MISSING_CLIENT_TOKEN, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException invalidTokenFormat) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_INVALID_TOKEN_FORMAT, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
        boolean tokenValid = checkToken(uuid);
        if (!tokenValid) {
            ResponseEntity responseEntity = new ResponseEntity(ResponseConstant.STYX_TOKEN_EXPIRED_OR_REVOKED, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
            throw new StyxException(responseEntity);
        }
    }
}
