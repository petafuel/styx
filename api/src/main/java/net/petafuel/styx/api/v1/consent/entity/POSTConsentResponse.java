package net.petafuel.styx.api.v1.consent.entity;

import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.SCA;


public class POSTConsentResponse {
    private String consentId;
    private SCA.Approach aspspScaApproach;
    private Links links;
    private String psuMessage;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public SCA.Approach getAspspScaApproach() {
        return aspspScaApproach;
    }

    public void setAspspScaApproach(SCA.Approach aspspScaApproach) {
        this.aspspScaApproach = aspspScaApproach;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }
}
