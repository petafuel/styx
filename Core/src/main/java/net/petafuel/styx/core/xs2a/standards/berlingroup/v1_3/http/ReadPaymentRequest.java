package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class ReadPaymentRequest extends XS2APaymentRequest {
    private String paymentId;

    public ReadPaymentRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, PSU psu) {
        super(paymentProduct, paymentService, psu);
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
