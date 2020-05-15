package net.petafuel.styx.core.xs2a.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for Account objects
 *
 * <p>
 * Access is used to describe all Accounts that are involved in consent-creation
 * </p>
 *
 * @see Account
 */
public class Access {
    private List<Account> balances;
    private List<Account> transactions;
    private List<Account> accounts;

    public Access() {
        balances = new ArrayList<>();
        transactions = new ArrayList<>();
        accounts = new ArrayList<>();
    }

    public void addBalanceAccounts(List<Account> accounts) {
        balances.addAll(accounts);
    }

    public void addBalanceAccount(Account account) {
        this.balances.add(account);
    }

    public void addTransactionAccounts(List<Account> accounts) {
        transactions.addAll(accounts);
    }

    public void addTransactionAccount(Account account) {
        this.transactions.add(account);
    }

    public List<Account> getBalances() {
        return balances;
    }

    public void setBalances(List<Account> balances) {
        this.balances = balances;
    }

    public List<Account> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Account> transactions) {
        this.transactions = transactions;
    }

    public void addAccounts(List<Account> accounts) {
        this.accounts.addAll(accounts);
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}