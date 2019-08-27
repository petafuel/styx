package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;

public class ReadAccountListRequest extends XS2AGetRequest {
    @XS2AQueryParameter("withBalance")
    private boolean withBalance;

    public ReadAccountListRequest(String consentId) {
        super(consentId);
        this.withBalance = false;
    }

    public boolean isWithBalance() {
        return withBalance;
    }

    public void setWithBalance(boolean withBalance) {
        this.withBalance = withBalance;
    }
}
