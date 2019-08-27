package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;

public class ReadAccountDetailsRequest extends XS2AGetRequest {

    /**
     * Body
     */

    @XS2AQueryParameter("withBalance")
    private boolean withBalance;
    private String accountId;

    public ReadAccountDetailsRequest(String accountId, String consentId) {
        super(consentId);
        this.accountId = accountId;
        this.withBalance = false;
    }

    public boolean isWithBalance() {
        return withBalance;
    }

    public void setWithBalance(boolean withBalance) {
        this.withBalance = withBalance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
