package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupPIS;

public class ReadPaymentStatusRequest extends XS2AGetRequest {

    private BerlinGroupPIS.PaymentService paymentService;
    private BerlinGroupPIS.PaymentProduct paymentProduct;
    private String paymentId;

    public ReadPaymentStatusRequest(BerlinGroupPIS.PaymentService paymentService, BerlinGroupPIS.PaymentProduct paymentProduct, String paymentId) {
        super();
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
    }

    public BerlinGroupPIS.PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(BerlinGroupPIS.PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BerlinGroupPIS.PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(BerlinGroupPIS.PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
