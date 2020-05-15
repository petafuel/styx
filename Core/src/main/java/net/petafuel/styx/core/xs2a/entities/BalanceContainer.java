package net.petafuel.styx.core.xs2a.entities;

import java.util.List;

/**
 * Holds account with connected balances
 */
public class BalanceContainer {
    private AccountReference account;
    private List<Balance> balances;

    public AccountReference getAccount() {
        return account;
    }

    public void setAccount(AccountReference account) {
        this.account = account;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }
}
