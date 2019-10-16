package net.petafuel.styx.core.xs2a.entities;

public class PaymentStatus {

    private Transaction.Status transactionStatus;
    private Boolean fundsAvailable;

    public PaymentStatus(Transaction.Status transactionStatus, Boolean fundsAvailable) {
        this.transactionStatus = transactionStatus;
        this.fundsAvailable = fundsAvailable;
    }

    public Transaction.Status getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Transaction.Status transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Boolean getFundsAvailable() {
        return fundsAvailable;
    }

    public void setFundsAvailable(Boolean fundsAvailable) {
        this.fundsAvailable = fundsAvailable;
    }
}
