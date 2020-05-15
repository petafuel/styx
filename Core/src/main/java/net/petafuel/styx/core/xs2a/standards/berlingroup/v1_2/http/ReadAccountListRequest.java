package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class ReadAccountListRequest extends XS2ARequest {
    @XS2AQueryParameter("withBalance")
    private boolean withBalance;

    public ReadAccountListRequest(String consentId) {
        this.withBalance = false;
        this.setConsentId(consentId);
    }

    public boolean isWithBalance() {
        return withBalance;
    }

    public void setWithBalance(boolean withBalance) {
        this.withBalance = withBalance;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
