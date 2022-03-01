package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesAIS {
    @JsonbProperty("accountDetails")
    private boolean accountDetails;

    @JsonbProperty("accountList")
    private boolean accountList;

    @JsonbProperty("accountsWithBalance")
    private boolean accountsWithBalance;

    @JsonbProperty("accountsAccountIdWithBalance")
    private boolean accountsAccountIdWithBalance;

    @JsonbProperty("accountsAccountIdTransactionsWithBalance")
    private boolean accountsAccountIdTransactionsWithBalance;

    @JsonbProperty("accountsAccountIdTransactionsResourceId")
    private boolean accountsAccountIdTransactionsResourceId;

    public SupportedServicesAIS() {
        // default constructor for json binding
    }

    public SupportedServicesAIS(boolean accountDetails, boolean accountList, boolean accountsWithBalance, boolean accountsAccountIdWithBalance, boolean accountsAccountIdTransactionsWithBalance, boolean accountsAccountIdTransactionsResourceId) {
        this.accountDetails = accountDetails;
        this.accountList = accountList;
        this.accountsWithBalance = accountsWithBalance;
        this.accountsAccountIdWithBalance = accountsAccountIdWithBalance;
        this.accountsAccountIdTransactionsWithBalance = accountsAccountIdTransactionsWithBalance;
        this.accountsAccountIdTransactionsResourceId = accountsAccountIdTransactionsResourceId;
    }

    public boolean getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(boolean accountDetails) {
        this.accountDetails = accountDetails;
    }

    public boolean getAccountList() {
        return accountList;
    }

    public void setAccountList(boolean accountList) {
        this.accountList = accountList;
    }

    public boolean getAccountsWithBalance() {
        return accountsWithBalance;
    }

    public void setAccountsWithBalance(boolean accountsWithBalance) {
        this.accountsWithBalance = accountsWithBalance;
    }

    public boolean getAccountsAccountIdWithBalance() {
        return accountsAccountIdWithBalance;
    }

    public void setAccountsAccountIdWithBalance(boolean accountsAccountIdWithBalance) {
        this.accountsAccountIdWithBalance = accountsAccountIdWithBalance;
    }

    public boolean getAccountsAccountIdTransactionsWithBalance() {
        return accountsAccountIdTransactionsWithBalance;
    }

    public void setAccountsAccountIdTransactionsWithBalance(boolean accountsAccountIdTransactionsWithBalance) {
        this.accountsAccountIdTransactionsWithBalance = accountsAccountIdTransactionsWithBalance;
    }

    public boolean getAccountsAccountIdTransactionsResourceId() {
        return accountsAccountIdTransactionsResourceId;
    }

    public void setAccountsAccountIdTransactionsResourceId(boolean accountsAccountIdTransactionsResourceId) {
        this.accountsAccountIdTransactionsResourceId = accountsAccountIdTransactionsResourceId;
    }
}
