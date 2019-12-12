package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class ReadAccountDetailsRequest extends XS2ARequest {

    @XS2AQueryParameter("withBalance")
    private boolean withBalance;
    private String accountId;

    public ReadAccountDetailsRequest(String accountId, String consentId) {
        this.accountId = accountId;
        this.withBalance = false;
        this.setConsentId(consentId);
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

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
