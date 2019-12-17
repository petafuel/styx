package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.utils.DeserialisationHelper;

import java.lang.reflect.Type;

public class InitiatedPaymentSerializer implements JsonDeserializer<InitiatedPayment> {

    @Override
    public InitiatedPayment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            JsonObject object = json.getAsJsonObject();
            TransactionStatus transactionStatus = TransactionStatus.valueOf(object.get("transactionStatus").getAsString().toUpperCase());
            String paymentId = object.get("paymentId").getAsString();
            InitiatedPayment payment = new InitiatedPayment(paymentId, transactionStatus);
            JsonObject links = object.get("_links").getAsJsonObject();
            DeserialisationHelper.parseSCALinksData(payment.getSca(), links);
            return payment;
        } catch (Exception e) {
            throw new SerializerException("Unable to deserialize initiated payment", e);
        }
    }
}
