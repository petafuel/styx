package net.petafuel.styx.api.v1.consent.entity;

import net.petafuel.styx.core.xs2a.entities.Consent;

import javax.json.bind.annotation.JsonbProperty;

public class GetConsentStatusResponse {

    @JsonbProperty("consentStatus")
    private Consent.State state;

    public GetConsentStatusResponse() {

    }

    public GetConsentStatusResponse(Consent.State state) {
        this.state = state;
    }

    public Consent.State getState() {
        return state;
    }

    public void setState(Consent.State state) {
        this.state = state;
    }
}
