package net.petafuel.styx.core.xs2a.contracts;


import java.util.Optional;

public abstract class XS2AGetRequest extends XS2ARequest {

    private String consentIdentifier;

    /**
     * @param consentId specifies the consent ID that will be retrieved from the ASPSP
     */
    public XS2AGetRequest(String consentId) {
        this.consentIdentifier = consentId;
    }

    public XS2AGetRequest() {
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.of("");
    }

    @Override
    public String getConsentId() {
        return this.consentIdentifier;
    }

    @Override
    public void setConsentId(String consentId) {
        this.consentIdentifier = consentId;
    }
}
