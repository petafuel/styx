package net.petafuel.styx.core.xs2a.entities;


import java.util.List;

public class AccountListResponse {
    private List<Account> accounts;

    public AccountListResponse(List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Default constructor for json-binding
     */
    public AccountListResponse() {}

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

}
