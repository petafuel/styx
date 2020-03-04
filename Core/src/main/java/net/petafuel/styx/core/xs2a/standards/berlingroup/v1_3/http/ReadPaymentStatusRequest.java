package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class ReadPaymentStatusRequest extends XS2APaymentRequest {
    private String paymentId;

    public ReadPaymentStatusRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentProduct, paymentService, null);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
