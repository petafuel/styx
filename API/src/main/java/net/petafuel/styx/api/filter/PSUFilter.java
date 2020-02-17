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
@RequiresPSU
public class PSUFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String psuId = containerRequestContext.getHeaderString(XS2AHeader.PSU_ID);
        if (psuId == null || "".equals(psuId)) {
            throw new StyxException(new ErrorEntity("PSU-ID is not provided or empty", Response.Status.BAD_REQUEST, ErrorCategory.CLIENT));
        }
    }
}
