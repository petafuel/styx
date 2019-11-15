package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class PeriodicPaymentMultipartBodySerializer implements JsonSerializer<PeriodicPayment>{

	@Override
	public JsonElement serialize(PeriodicPayment payment, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject object = new JsonObject();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		//Periodic Payment Serialization
		String formattedStartDate = format.format(payment.getStartDate());
		object.addProperty("startDate", formattedStartDate);
		if(payment.getExecutionRule() != null) {
			object.addProperty("executionRule", payment.getExecutionRule().toString());
		}
		if (payment.getEndDate() != null) {
			String formattedEndDate = format.format(payment.getEndDate());
			object.addProperty("endDate", formattedEndDate);
		}
		object.addProperty("frequency", payment.getFrequency().toString());
		object.addProperty("dayOfExecution", payment.getDayOfExecution());

		return object;
	}
}
