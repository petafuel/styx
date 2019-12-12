package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class PaymentSerializer implements JsonSerializer<Payment> {

    @Override
    public JsonElement serialize(Payment payment, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        JsonObject creditorAccount = new JsonObject();
        JsonObject debtorAccount = new JsonObject();
        JsonObject instructedAmount = new JsonObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        creditorAccount.addProperty(payment.getCreditor().getType().getJsonKey(), payment.getCreditor().getIdentifier());
        creditorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCreditor().getCurrency().toString());
        object.add("creditorAccount", creditorAccount);
        debtorAccount.addProperty(payment.getDebtor().getType().getJsonKey(), payment.getDebtor().getIdentifier());
        debtorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getDebtor().getCurrency().toString());
        object.addProperty("creditorName", payment.getCreditor().getName());
        object.add("debtorAccount", debtorAccount);
        instructedAmount.addProperty("amount", payment.getAmount());
        instructedAmount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCurrency().toString());
        object.add("instructedAmount", instructedAmount);

        if (payment.getRequestedExecutionDate() != null) {
            String formattedDate = format.format(payment.getRequestedExecutionDate());
            object.addProperty("requestedExecutionDate", formattedDate);
        }

        object.addProperty("remittanceInformationUnstructured", payment.getReference());

        return object;
    }

}
