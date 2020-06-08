package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
 * Container to hold relevant transaction data returned from ASPSP Interface
 */
public class TransactionContainer {
    private AccountReference account;
    private AccountReport transactions;
    private List<Balance> balances;
    private Links links;

    @JsonbProperty("links")
    public Links getLinks() {
        return links;
    }

    @JsonbProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }

    public AccountReference getAccount() {
        return account;
    }

    public void setAccount(AccountReference account) {
        this.account = account;
    }

    public AccountReport getTransactions() {
        return transactions;
    }

    public void setTransactions(AccountReport transactions) {
        this.transactions = transactions;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }
}
