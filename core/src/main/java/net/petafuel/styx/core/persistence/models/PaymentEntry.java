package net.petafuel.styx.core.persistence.models;

import net.petafuel.styx.core.persistence.DatabaseColumn;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;

import java.util.Date;

public class PaymentEntry {
    @DatabaseColumn("id")
    private String id;
    @DatabaseColumn("payment_id")
    private String paymentId;
    private AccessToken clientToken;
    @DatabaseColumn("bic")
    private String bic;
    private TransactionStatus status;
    @DatabaseColumn("created_at")
    private Date createdAt;
    @DatabaseColumn("updated_at")
    private Date updatedAt;
    //will be set within PersistentPayment
    private PaymentService paymentService;
    //will be set within PersistentPayment
    private PaymentProduct paymentProduct;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public AccessToken getClientToken() {
        return clientToken;
    }

    public void setClientToken(AccessToken clientToken) {
        this.clientToken = clientToken;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }
}
