package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Optional;

public class PeriodicPaymentInitiationJsonRequest extends XS2APaymentRequest {

    private PeriodicPayment payment;

    public PeriodicPaymentInitiationJsonRequest(PaymentProduct paymentProduct, PeriodicPayment payment, PSU psu) {
        super(paymentProduct, PaymentService.PERIODIC_PAYMENTS, psu);
        this.payment = payment;
    }

    @Override
    public Optional<String> getRawBody() {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            return Optional.ofNullable(jsonb.toJson(payment));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public PeriodicPayment getPayment() {
        return payment;
    }

    public void setPayment(PeriodicPayment payment) {
        this.payment = payment;
    }
}
