package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import java.lang.reflect.Type;

public class PaymentInitiationJsonRequestSerializer implements JsonSerializer<PaymentInitiationJsonRequest>{
	@Override
	public JsonElement serialize(PaymentInitiationJsonRequest src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject object = new JsonObject();
		JsonObject creditorAccount = new JsonObject();
		JsonObject debtorAccount = new JsonObject();
		JsonObject instructedAmount = new JsonObject();

		creditorAccount.addProperty(src.getBody().getCreditor().getType().getJsonKey(), src.getBody().getCreditor().getIdentifier());
		creditorAccount.addProperty("currency", src.getBody().getCreditor().getCurrency().toString());
		object.add("creditorAccount", creditorAccount);
		debtorAccount.addProperty(src.getBody().getDebtor().getType().getJsonKey(), src.getBody().getDebtor().getIdentifier());
		debtorAccount.addProperty("currency", src.getBody().getDebtor().getCurrency().toString());
        object.addProperty("creditorName", src.getBody().getCreditor().getName());
		object.add("debtorAccount", debtorAccount);
		instructedAmount.addProperty("amount", src.getBody().getAmount());
		instructedAmount.addProperty("currency", src.getBody().getCurrency().toString());
		object.add("instructedAmount", instructedAmount);
		object.addProperty("remittanceInformationUnstructured", src.getBody().getReference());

		return object;
	}

}
