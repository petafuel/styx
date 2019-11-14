package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PeriodicPaymentSerializer;

public class PeriodicPaymentInitiationJsonRequest extends XS2APaymentInitiationRequest {

    private PeriodicPayment payment;

    public PeriodicPaymentInitiationJsonRequest(PaymentProduct paymentProduct, PeriodicPayment payment, PSU psu) {
        super(paymentProduct, PaymentService.PERIODIC_PAYMENTS, psu);
        this.payment = payment;
    }

    @Override
    public String getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PeriodicPaymentSerializer()).create();
        return gson.toJson(this.payment);
    }

    public PeriodicPayment getPayment() {
        return payment;
    }

    public void setPayment(PeriodicPayment payment) {
        this.payment = payment;
    }
}
