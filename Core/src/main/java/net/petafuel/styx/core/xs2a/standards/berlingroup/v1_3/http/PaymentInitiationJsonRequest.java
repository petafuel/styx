package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class PaymentInitiationJsonRequest extends XS2APaymentInitiationRequest {

    private Payment payment;

    public PaymentInitiationJsonRequest(PaymentProduct paymentProduct, Payment payment, PSU psu) {
        super(paymentProduct, PaymentService.PAYMENTS, psu);
        this.payment = payment;
    }

    @Override
    public Optional<String> getRawBody() {
        JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.withNullValues(false);
        try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
            return Optional.ofNullable(jsonb.toJson(payment));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
