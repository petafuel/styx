package net.petafuel.styx.api.rest;

import javax.ws.rs.HeaderParam;

public abstract class RestResource {
    @HeaderParam("redirectPreferred")
    private Boolean redirectPreferred;

    public Boolean getRedirectPreferred() {
        return redirectPreferred;
    }

    public void setRedirectPreferred(Boolean redirectPreferred) {
        this.redirectPreferred = redirectPreferred;
    }
}
