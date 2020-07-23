package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

public abstract class StrongAuthenticatableResource implements XS2AResponse{
    @JsonbTransient
    protected SCA sca;

    private String psuMessage;
    
    private Links links;

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }

    public final SCA getSca() {
        return sca;
    }

    public final void setSca(SCA sca) {
        this.sca = sca;
    }
    
    @JsonbProperty("links")
    public Links getLinks() {
        return links;
    }
    
    @JsonbProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }
}
