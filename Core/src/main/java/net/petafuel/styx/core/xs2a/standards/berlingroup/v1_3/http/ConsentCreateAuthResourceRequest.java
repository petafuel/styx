package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class ConsentCreateAuthResourceRequest extends XS2ARequest {
    public ConsentCreateAuthResourceRequest(String consentId) {
        this.setConsentId(consentId);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
