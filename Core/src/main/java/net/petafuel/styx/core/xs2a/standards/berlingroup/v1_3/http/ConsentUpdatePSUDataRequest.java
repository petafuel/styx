package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSUData;

import java.util.Optional;

public class ConsentUpdatePSUDataRequest extends XS2ARequest {

    //this is a path parameter and should not be parsed for header lines or query params
    String consentIdentifier;
    String authorisationId;

    //Body
    PSUData psuData;

    public ConsentUpdatePSUDataRequest(String consentId, String authorisationId) {
        this.authorisationId = authorisationId;
        consentIdentifier = consentId;
        psuData = new PSUData();
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
    }

    @Override
    public String getConsentId() {
        return consentIdentifier;
    }

    @Override
    public void setConsentId(String consentId) {
        this.consentIdentifier = consentId;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
