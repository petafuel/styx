package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;

public class PaymentStatus {

    @JsonbProperty(value = "transactionStatus", nillable = true)
    private TransactionStatus transactionStatus;

    @JsonbProperty(value = "fundsAvailable", nillable = true)
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
