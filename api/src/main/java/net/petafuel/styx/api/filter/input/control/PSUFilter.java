package net.petafuel.styx.api.filter.input.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.filter.input.boundary.RequiresPSU;
import net.petafuel.styx.api.rest.StyxFilterPriorites;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.PSU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
@RequiresPSU
@Priority(StyxFilterPriorites.INPUT_VALIDATION)
public class PSUFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(PSUFilter.class);

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String id = containerRequestContext.getHeaderString(XS2AHeader.PSU_ID);
        if (id == null || "".equals(id)) {
            throw new StyxException(new ResponseEntity("PSU-ID is not provided or empty", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
        PSU psu = new PSU(id);
        psu.setIdType(containerRequestContext.getHeaderString(XS2AHeader.PSU_ID_TYPE));
        psu.setCorporateId(containerRequestContext.getHeaderString(XS2AHeader.PSU_CORPORATE_ID));
        psu.setCorporateIdType(containerRequestContext.getHeaderString(XS2AHeader.PSU_CORPORATE_ID_TYPE));
        psu.setIp(httpServletRequest.getRemoteAddr());

        String psuPort = containerRequestContext.getHeaderString(XS2AHeader.PSU_IP_PORT);
        try {
            Integer intVal = psuPort != null ? Integer.valueOf(psuPort) : null;
            psu.setPort(intVal);
        } catch (NumberFormatException invalidDataType) {
            LOG.warn("PSU-PORT header was bigger than integer MAX_VALUE, not accepting header value");
        }
        psu.setUserAgent(containerRequestContext.getHeaderString(XS2AHeader.PSU_USER_AGENT));
        psu.setGeoLocation(containerRequestContext.getHeaderString(XS2AHeader.PSU_GEO_LOCATION));
        containerRequestContext.setProperty(PSU.class.getName(), psu);
    }
}
