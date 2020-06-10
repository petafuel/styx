package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;

import java.lang.reflect.Type;

public class PaymentStatusSerializer implements JsonDeserializer<PaymentStatus> {

    public PaymentStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject response = json.getAsJsonObject();
        TransactionStatus status = TransactionStatus.valueOf(response.get("transactionStatus").getAsString());

        JsonElement fundsAvailable = response.get("fundsAvailable");
        Boolean funds = (fundsAvailable != null) ? fundsAvailable.getAsBoolean() : null;

        return new PaymentStatus(status, funds);
    }
}
