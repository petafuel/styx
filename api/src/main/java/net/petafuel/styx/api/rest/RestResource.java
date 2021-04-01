package net.petafuel.styx.api.rest;

import net.petafuel.styx.api.filter.authentication.control.PreAuthAccessFilter;
import net.petafuel.styx.api.filter.input.control.SandboxHeaderPassthroughs;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;

public abstract class RestResource {
    private static final Logger LOG = LogManager.getLogger(RestResource.class);

    @Context
    private ContainerRequestContext containerRequestContext;

    @HeaderParam("redirectPreferred")
    private Boolean redirectPreferred;

    public Boolean getRedirectPreferred() {
        return redirectPreferred;
    }

    public void setRedirectPreferred(Boolean redirectPreferred) {
        this.redirectPreferred = redirectPreferred;
    }

    public ContainerRequestContext getContainerRequestContext() {
        return containerRequestContext;
    }

    public Object getContextProperty(String propertyName) {
        return containerRequestContext.getProperty(propertyName);
    }

    protected XS2AStandard getXS2AStandard() {
        Object xs2AStandard = containerRequestContext.getProperty(XS2AStandard.class.getName());
        if (xs2AStandard != null) {
            return (XS2AStandard) xs2AStandard;
        } else {
            LOG.warn("XS2AStandard was requested in Endpoint Resource but was not previously initiated");
            return null;
        }

    }

    protected PSU getPsu() {
        Object psu = containerRequestContext.getProperty(PSU.class.getName());
        if (psu != null) {
            return (PSU) psu;
        } else {
            LOG.warn("PSU was requested in Endpoint Resource but was not previously initiated");
            return null;
        }
    }

    //Null checked after type cast
    @SuppressWarnings("unchecked")
    public Map<String, String> getAdditionalHeaders() {
        Map<String, String> additionalHeaders = new HashMap<>();
        if (getContainerRequestContext().getProperty(SandboxHeaderPassthroughs.class.getName()) != null) {
            additionalHeaders.putAll((Map<String, String>) getContainerRequestContext().getProperty(SandboxHeaderPassthroughs.class.getName()));
        }
        if (getContainerRequestContext().getProperty(PreAuthAccessFilter.class.getName()) != null) {
            additionalHeaders.putAll((Map<String, String>) getContainerRequestContext().getProperty(PreAuthAccessFilter.class.getName()));
        }
        return additionalHeaders;
    }
}
