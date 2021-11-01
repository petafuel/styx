package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesAIS {
    @JsonbProperty("accountDetails")
    private Boolean accountDetails;

    @JsonbProperty("accountList")
    private Boolean accountList;

    @JsonbProperty("accountsWithBalance")
    private Boolean accountsWithBalance;

    @JsonbProperty("accountsAccountIdWithBalance")
    private Boolean accountsAccountIdWithBalance;

    @JsonbProperty("accountsAccountIdTransactionsWithBalance")
    private Boolean accountsAccountIdTransactionsWithBalance;

    @JsonbProperty("accountsAccountIdTransactionsResourceId")
    private Boolean accountsAccountIdTransactionsResourceId;

    public SupportedServicesAIS() {
        // default constructor for json binding
    }

    public SupportedServicesAIS(Boolean accountDetails, Boolean accountList, Boolean accountsWithBalance, Boolean accountsAccountIdWithBalance, Boolean accountsAccountIdTransactionsWithBalance, Boolean accountsAccountIdTransactionsResourceId) {
        this.accountDetails = accountDetails;
        this.accountList = accountList;
        this.accountsWithBalance = accountsWithBalance;
        this.accountsAccountIdWithBalance = accountsAccountIdWithBalance;
        this.accountsAccountIdTransactionsWithBalance = accountsAccountIdTransactionsWithBalance;
        this.accountsAccountIdTransactionsResourceId = accountsAccountIdTransactionsResourceId;
    }

    public Boolean getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(Boolean accountDetails) {
        this.accountDetails = accountDetails;
    }

    public Boolean getAccountList() {
        return accountList;
    }

    public void setAccountList(Boolean accountList) {
        this.accountList = accountList;
    }

    public Boolean getAccountsWithBalance() {
        return accountsWithBalance;
    }

    public void setAccountsWithBalance(Boolean accountsWithBalance) {
        this.accountsWithBalance = accountsWithBalance;
    }

    public Boolean getAccountsAccountIdWithBalance() {
        return accountsAccountIdWithBalance;
    }

    public void setAccountsAccountIdWithBalance(Boolean accountsAccountIdWithBalance) {
        this.accountsAccountIdWithBalance = accountsAccountIdWithBalance;
    }

    public Boolean getAccountsAccountIdTransactionsWithBalance() {
        return accountsAccountIdTransactionsWithBalance;
    }

    public void setAccountsAccountIdTransactionsWithBalance(Boolean accountsAccountIdTransactionsWithBalance) {
        this.accountsAccountIdTransactionsWithBalance = accountsAccountIdTransactionsWithBalance;
    }

    public Boolean getAccountsAccountIdTransactionsResourceId() {
        return accountsAccountIdTransactionsResourceId;
    }

    public void setAccountsAccountIdTransactionsResourceId(Boolean accountsAccountIdTransactionsResourceId) {
        this.accountsAccountIdTransactionsResourceId = accountsAccountIdTransactionsResourceId;
    }
}
