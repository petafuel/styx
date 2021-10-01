package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.entities.Consent;

import java.util.Optional;

public class ReadBalancesRequest extends AISRequest {
    public ReadBalancesRequest(Consent consent, String consentId, String accountId, String transactionId) {
        super(consent, consentId, accountId, transactionId);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public BasicService.RequestType getHttpMethod() {
        return BasicService.RequestType.GET;
    }

    @Override
    public String getServicePath() {
        return String.format("/v1/accounts/%s/balances", getAccountId());
    }
}
