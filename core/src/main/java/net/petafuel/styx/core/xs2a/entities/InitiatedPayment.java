package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class InitiatedPayment extends StrongAuthenticatableResource implements Serializable {

    private String paymentId;
    @JsonbProperty("transactionStatus")
    private TransactionStatus status;
    private PSU psu;

    public InitiatedPayment() {
        this.sca = new SCA();
    }

    public InitiatedPayment(String paymentId, TransactionStatus status) {
        this.paymentId = paymentId;
        this.status = status;
        this.sca = new SCA();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }
}
