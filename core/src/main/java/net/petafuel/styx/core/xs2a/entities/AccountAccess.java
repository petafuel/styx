package net.petafuel.styx.core.xs2a.entities;

import java.util.List;

/**
 * Defines accesslevels that a consent will be created with
 * accounts -> grants generic /account route access
 * balances -> implicitly holds accounts, grants access to account balances
 * transactions -> implicitly holds accounts, grands access to account transactions/revenues
 */
public class AccountAccess {
    private List<AccountReference> accounts;
    private List<AccountReference> balances;
    private List<AccountReference> transactions;
    private AdditionalInformationAccess additionalInformation;
    private String availableAccounts;
    private String availableAccountsWithBalance;
    private String allPsd2;

    public List<AccountReference> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountReference> accounts) {
        this.accounts = accounts;
    }

    public List<AccountReference> getBalances() {
        return balances;
    }

    public void setBalances(List<AccountReference> balances) {
        this.balances = balances;
    }

    public List<AccountReference> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AccountReference> transactions) {
        this.transactions = transactions;
    }

    public AdditionalInformationAccess getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformationAccess additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAvailableAccounts() {
        return availableAccounts;
    }

    public void setAvailableAccounts(String availableAccounts) {
        this.availableAccounts = availableAccounts;
    }

    public String getAvailableAccountsWithBalance() {
        return availableAccountsWithBalance;
    }

    public void setAvailableAccountsWithBalance(String availableAccountsWithBalance) {
        this.availableAccountsWithBalance = availableAccountsWithBalance;
    }

    public String getAllPsd2() {
        return allPsd2;
    }

    public void setAllPsd2(String allPsd2) {
        this.allPsd2 = allPsd2;
    }
}
