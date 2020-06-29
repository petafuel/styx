package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;

import java.util.Optional;

public class DeleteConsentRequest extends AISRequest {
    public DeleteConsentRequest(Consent consent, String consentId, String accountId, String transactionId) {
        super(consent, consentId, accountId, transactionId);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public String getServicePath() {
        return String.format("/v1/consents/%s", getConsentId());
    }

}
