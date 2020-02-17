package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ErrorCategory;
import net.petafuel.styx.api.exception.ErrorEntity;
import net.petafuel.styx.api.exception.StyxException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.util.UUID;

public abstract class AbstractTokenFilter implements ContainerRequestFilter {

    public abstract boolean checkToken(UUID uuid);

    @Override
    public void filter(ContainerRequestContext context) {
        String token = context.getHeaderString("token");
        if (token == null || "".equals(token)) {
            ErrorEntity errorEntity = new ErrorEntity("Token was not contained or empty in request", Response.Status.BAD_REQUEST, ErrorCategory.CLIENT);
            throw new StyxException(errorEntity);
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException invalidTokenFormat) {
            ErrorEntity errorEntity = new ErrorEntity("Token value has an invalid format", Response.Status.BAD_REQUEST, ErrorCategory.CLIENT);
            throw new StyxException(errorEntity);
        }
        boolean tokenValid = checkToken(uuid);
        if (!tokenValid) {
            ErrorEntity errorEntity = new ErrorEntity("Token is invalid", Response.Status.UNAUTHORIZED, ErrorCategory.CLIENT);
            throw new StyxException(errorEntity);
        }
    }
}
