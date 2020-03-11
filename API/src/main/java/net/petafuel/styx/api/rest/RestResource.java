package net.petafuel.styx.api.rest;

import net.petafuel.styx.api.filter.SandboxHeaderPassthroughs;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;

public abstract class RestResource {
    @Context
    ContainerRequestContext containerRequestContext;

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

    //Null checked after type cast
    @SuppressWarnings("unchecked")
    public Map<String, String> getSandboxHeaders() {
        Map<String, String> sandboxHeaders = (Map<String, String>) getContainerRequestContext().getProperty(SandboxHeaderPassthroughs.class.getName());
        if (sandboxHeaders != null) {
            return sandboxHeaders;
        } else {
            return new HashMap<>();
        }
    }
}
