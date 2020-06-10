package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.rest.StyxFilterPriorites;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@RequiresMandatoryHeader
@Priority(StyxFilterPriorites.INPUT_VALIDATION)
public class MandatoryHeaderFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String redirectPreferred = containerRequestContext.getHeaderString(XS2AHeader.REDIRECT_PREFERRED);
        if (redirectPreferred == null || "".equals(redirectPreferred)) {
            throw new StyxException(new ResponseEntity("header redirectPreferred was not set", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
    }
}
