package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@RequiresBIC
public class BICFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String bic = containerRequestContext.getHeaderString(XS2AHeader.PSU_BIC);
        if (bic == null || "".equals(bic)) {
            throw new StyxException(new ResponseEntity("PSU-BIC is not provided or empty", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
    }
}
