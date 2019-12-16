package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import java.lang.reflect.Type;

public class PeriodicPaymentSerializer implements JsonSerializer<PeriodicPayment> {

    @Override
    public JsonElement serialize(PeriodicPayment payment, Type typeOfSrc, JsonSerializationContext context) {

        Gson gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PaymentSerializer()).create();

        JsonObject paymentJsonObject = gson.toJsonTree(payment).getAsJsonObject();

        gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PeriodicPaymentMultipartBodySerializer()).create();
        JsonObject periodicJsonObject = gson.toJsonTree(payment).getAsJsonObject();

        //merge payment data and period data together
        periodicJsonObject.entrySet().forEach(jsonElementEntry -> paymentJsonObject.add(jsonElementEntry.getKey(), jsonElementEntry.getValue()));

        return paymentJsonObject;
    }
}
