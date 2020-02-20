package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class GetConsentRequest extends XS2ARequest {
    //This is a path parameter and should not be parsed for header lines or query params
    private String consentIdentifier;

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public String getConsentId() {
        return consentIdentifier;
    }

    @Override
    public void setConsentId(String consentId) {
        this.consentIdentifier = consentId;
    }
}
