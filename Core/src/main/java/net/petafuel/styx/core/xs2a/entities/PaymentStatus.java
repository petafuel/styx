package net.petafuel.styx.core.xs2a.entities;

public class PaymentStatus {

    private TransactionStatus transactionStatus;
    private Boolean fundsAvailable;

    public PaymentStatus(TransactionStatus transactionStatus, Boolean fundsAvailable) {
        this.transactionStatus = transactionStatus;
        this.fundsAvailable = fundsAvailable;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Boolean getFundsAvailable() {
        return fundsAvailable;
    }

    public void setFundsAvailable(Boolean fundsAvailable) {
        this.fundsAvailable = fundsAvailable;
    }
}
