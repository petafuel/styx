package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;

public class PaymentSerializer implements JsonSerializer<Payment> {

    @Override
    public JsonElement serialize(Payment payment, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        JsonObject creditorAccount = new JsonObject();
        JsonObject debtorAccount = new JsonObject();
        JsonObject instructedAmount = new JsonObject();

        creditorAccount.addProperty(payment.getCreditor().getType().getJsonKey(), payment.getCreditor().getIdentifier());
        creditorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCreditor().getCurrency().toString());
        object.add("creditorAccount", creditorAccount);
        debtorAccount.addProperty(payment.getDebtor().getType().getJsonKey(), payment.getDebtor().getIdentifier());
        debtorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getDebtor().getCurrency().toString());
        object.addProperty("creditorName", payment.getCreditor().getName());
        object.add("debtorAccount", debtorAccount);
        instructedAmount.addProperty("amount", payment.getInstructedAmount().getAmount());
        instructedAmount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getInstructedAmount().getCurrency().name());
        object.add("instructedAmount", instructedAmount);

        object.addProperty("remittanceInformationUnstructured", payment.getRemittanceInformationUnstructured());

        return object;
    }

}
