package net.petafuel.styx.api.v1.consent.entity;

import net.petafuel.styx.core.xs2a.entities.Consent;

public class GetConsentResponse {

    private Consent consent;

    public GetConsentResponse(Consent consent) {
        this.consent = consent;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }
}
