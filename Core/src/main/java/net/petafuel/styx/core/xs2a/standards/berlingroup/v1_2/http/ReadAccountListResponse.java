package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.entities.AccountDetails;

import java.util.List;

public class ReadAccountListResponse {
    private List<AccountDetails> accounts;

    public List<AccountDetails> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDetails> accounts) {
        this.accounts = accounts;
    }
}

