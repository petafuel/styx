package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.entities.Transaction;

public class ReadTransactionDetailsResponse {
    Transaction transactionsDetails;

    public Transaction getTransactionsDetails() {
        return transactionsDetails;
    }

    public void setTransactionsDetails(Transaction transactionsDetails) {
        this.transactionsDetails = transactionsDetails;
    }
}
