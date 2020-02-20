package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class ReadPaymentStatusRequest extends XS2ARequest {

    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;

    public ReadPaymentStatusRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
