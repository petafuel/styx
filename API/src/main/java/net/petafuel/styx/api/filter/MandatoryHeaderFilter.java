package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ErrorCategory;
import net.petafuel.styx.api.exception.ErrorEntity;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@RequiresMandatoryHeader
public class MandatoryHeaderFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String redirectPreferred = containerRequestContext.getHeaderString(XS2AHeader.REDIRECT_PREFERRED);
        if (redirectPreferred == null || "".equals(redirectPreferred)) {
            throw new StyxException(new ErrorEntity("header redirectPreferred was not set", Response.Status.BAD_REQUEST, ErrorCategory.CLIENT));
        }
    }
}
