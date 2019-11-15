package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PaymentSerializer;

public class PaymentInitiationJsonRequest extends XS2APaymentInitiationRequest {

	private Payment payment;

	public PaymentInitiationJsonRequest(PaymentProduct paymentProduct, Payment payment, PSU psu) {
		super(paymentProduct, PaymentService.PAYMENTS, psu);
		this.payment = payment;
	}

	@Override
	public String getRawBody() {
		Gson gson = new GsonBuilder().registerTypeAdapter(Payment.class, new PaymentSerializer()).create();
		return gson.toJson(this.payment);
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
