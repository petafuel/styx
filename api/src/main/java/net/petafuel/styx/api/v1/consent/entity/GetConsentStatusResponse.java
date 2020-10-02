package net.petafuel.styx.api.v1.consent.entity;

import net.petafuel.styx.core.xs2a.entities.ConsentStatus;

import javax.json.bind.annotation.JsonbProperty;

public class GetConsentStatusResponse {

    @JsonbProperty("consentStatus")
    private ConsentStatus state;

    public GetConsentStatusResponse() {

    }

    public GetConsentStatusResponse(ConsentStatus state) {
        this.state = state;
    }

    public ConsentStatus getState() {
        return state;
    }

    public void setState(ConsentStatus state) {
        this.state = state;
    }
}
