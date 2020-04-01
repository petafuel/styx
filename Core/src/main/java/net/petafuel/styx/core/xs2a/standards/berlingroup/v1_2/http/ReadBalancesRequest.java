package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class ReadBalancesRequest extends XS2ARequest {

    /**
     * Body
     */
    private String accountId;

    public ReadBalancesRequest(String accountId, String consentId) {
        this.accountId = accountId;
        this.setConsentId(consentId);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
