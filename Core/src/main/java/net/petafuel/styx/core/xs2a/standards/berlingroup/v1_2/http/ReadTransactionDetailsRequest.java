package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class ReadTransactionDetailsRequest extends XS2ARequest {

    /**
     * Body
     */
    private String accountId;

    /**
     * This is the `transaction-id` used by the banks
     */
    private String transactionId;

    public ReadTransactionDetailsRequest(String accountId, String transactionId, String consentId) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.setConsentId(consentId);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
