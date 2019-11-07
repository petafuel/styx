package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentInitiationJsonRequestSerializer implements JsonSerializer<PaymentInitiationJsonRequest>{

	public static final String CURRENCY = "currency";

	@Override
	public JsonElement serialize(PaymentInitiationJsonRequest src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject object = new JsonObject();
		JsonObject creditorAccount = new JsonObject();
		JsonObject debtorAccount = new JsonObject();
		JsonObject instructedAmount = new JsonObject();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Payment payment = src.getPayment();

		creditorAccount.addProperty(payment.getCreditor().getType().getJsonKey(), payment.getCreditor().getIdentifier());
		creditorAccount.addProperty(CURRENCY, payment.getCreditor().getCurrency().toString());
		object.add("creditorAccount", creditorAccount);
		debtorAccount.addProperty(payment.getDebtor().getType().getJsonKey(), payment.getDebtor().getIdentifier());
		debtorAccount.addProperty(CURRENCY, payment.getDebtor().getCurrency().toString());
        object.addProperty("creditorName", payment.getCreditor().getName());
		object.add("debtorAccount", debtorAccount);
		instructedAmount.addProperty("amount", payment.getAmount());
		instructedAmount.addProperty(CURRENCY, payment.getCurrency().toString());
		object.add("instructedAmount", instructedAmount);

		if (src.getRequestedExecutionDate() != null) {
			String formattedDate = format.format(src.getRequestedExecutionDate());
			object.addProperty("requestedExecutionDate", formattedDate);
		} else {
			String formattedDate = format.format(new Date());
			object.addProperty("requestedExecutionDate", formattedDate);
		}

		object.addProperty("remittanceInformationUnstructured", payment.getReference());

		return object;
	}

}
