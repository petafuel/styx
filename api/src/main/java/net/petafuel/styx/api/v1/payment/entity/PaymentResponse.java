package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;

import javax.json.bind.annotation.JsonbProperty;

public class PaymentResponse {
    @JsonbProperty("transactionStatus")
    private TransactionStatus transactionStatus;

    @JsonbProperty("paymentId")
    private String paymentId;

    @JsonbProperty("links")
    private Links links;

    @JsonbProperty("psuMessage")
    private String psuMessage;

    public PaymentResponse() {
        //default ctor for json binding
    }

    public PaymentResponse(InitiatedPayment initiatedPayment) {
        transactionStatus = initiatedPayment.getStatus();
        paymentId = initiatedPayment.getPaymentId();
        links = initiatedPayment.getLinks();
        psuMessage = initiatedPayment.getSca().getPsuMessage();
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }
}
