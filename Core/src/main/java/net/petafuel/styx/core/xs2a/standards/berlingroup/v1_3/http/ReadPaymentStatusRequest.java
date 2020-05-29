package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class ReadPaymentStatusRequest extends PISRequest {
    public ReadPaymentStatusRequest(PaymentService paymentService, PaymentProduct paymentProduct, PSU psu, InitializablePayment payment) {
        super(paymentService, paymentProduct, psu, payment);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public String getServicePath() {
        return String.format("/v1/%s/%s/%s/status", getPaymentService().getValue(), getPaymentProduct().getValue(), getPaymentId());
    }
}
