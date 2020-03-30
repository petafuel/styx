package net.petafuel.styx.api.v1.consent.entity;

import net.petafuel.styx.core.xs2a.entities.AccountAccess;

import javax.validation.constraints.NotNull;

public class POSTConsentRequest {
    @NotNull
    private boolean recurringIndicator;

    @NotNull
    private AccountAccess access;

    private String availableAccounts;

    public boolean isRecurringIndicator() {
        return recurringIndicator;
    }

    public void setRecurringIndicator(boolean recurringIndicator) {
        this.recurringIndicator = recurringIndicator;
    }

    public AccountAccess getAccess() {
        return access;
    }

    public void setAccess(AccountAccess access) {
        this.access = access;
    }

    public String getAvailableAccounts() {
        return availableAccounts;
    }

    public void setAvailableAccounts(String availableAccounts) {
        this.availableAccounts = availableAccounts;
    }
}
