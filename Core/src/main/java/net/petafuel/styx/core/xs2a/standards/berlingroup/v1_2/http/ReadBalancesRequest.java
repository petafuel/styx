package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;

public class ReadBalancesRequest extends XS2AGetRequest {

    /**
     * Body
     */
    private String accountId;

    public ReadBalancesRequest(String accountId, String consentId) {
        super(consentId);
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
