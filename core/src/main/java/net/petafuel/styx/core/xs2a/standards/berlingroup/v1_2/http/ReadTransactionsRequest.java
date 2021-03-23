package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;

import java.util.Optional;

public class ReadTransactionsRequest extends AISRequest {

    private boolean isXml;

    public ReadTransactionsRequest(Consent consent, String consentId, String accountId, String transactionId) {
        super(consent, consentId, accountId, transactionId);
        isXml = false;
    }

    public boolean isXml() {
        return isXml;
    }

    public void setXml(boolean xml) {
        isXml = xml;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public String getServicePath() {
        return String.format("/v1/accounts/%s/transactions", getAccountId());
    }
}
