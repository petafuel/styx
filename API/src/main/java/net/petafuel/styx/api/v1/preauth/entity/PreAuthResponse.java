package net.petafuel.styx.api.v1.preauth.entity;

import net.petafuel.styx.core.xs2a.entities.Links;

public class PreAuthResponse {

    private String preAuthId;
    private Links links;

    /**
     * @deprecated default ctor for json binding
     */
    @Deprecated
    public PreAuthResponse() {
        //default ctor for json binding
    }

    public PreAuthResponse(String preAuthId, Links links) {
        this.preAuthId = preAuthId;
        this.links = links;
    }

    public String getPreAuthId() {
        return preAuthId;
    }

    public void setPreAuthId(String preAuthId) {
        this.preAuthId = preAuthId;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
