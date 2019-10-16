package net.petafuel.styx.core.xs2a.entities;

import java.io.Serializable;
import java.util.UUID;

public class InitiatedPayment implements Serializable {

    private String paymentId;
    private Status status;
    private SCA sca;
    private UUID xRequestId;
    private PSU psu;

    public enum Status {
        ACCP, ACSC, ACSP, ACTC, ACWC, ACWP, CANC, PNDG, RCVD, RJCT
    }

    public InitiatedPayment(String paymentId, Status status) {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SCA getSca() {
        return sca;
    }

    public void setSca(SCA sca) {
        this.sca = sca;
    }

    public UUID getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(UUID xRequestId) {
        this.xRequestId = xRequestId;
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }
}
