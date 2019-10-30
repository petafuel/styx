package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;

public class InitiatedPaymentSerializer implements JsonSerializer<InitiatedPayment>, JsonDeserializer<InitiatedPayment> {


    @Override
    public JsonElement serialize(InitiatedPayment src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonObject();
    }

    @Override
    public InitiatedPayment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            JsonObject object = json.getAsJsonObject();
            InitiatedPayment.Status transactionStatus = InitiatedPayment.Status.valueOf(object.get("transactionStatus").getAsString().toUpperCase());
            String paymentId = object.get("paymentId").getAsString();
            InitiatedPayment payment = new InitiatedPayment(paymentId, transactionStatus);
            JsonObject links = object.get("_links").getAsJsonObject();
            if (links.get(SCA.LinkType.SCA_REDIRECT.getJsonKey()) != null) {
                payment.getSca().setApproach(SCA.Approach.REDIRECT);
            } else if (links.get(SCA.LinkType.SCA_OAUTH.getJsonKey()) != null) {
                payment.getSca().setApproach(SCA.Approach.OAUTH2);
            }
            for (SCA.LinkType linkType: SCA.LinkType.values()) {
                if (links.get(linkType.getJsonKey()) != null) {
                    payment.getSca().addLink(linkType, links.get(linkType.getJsonKey()).getAsJsonObject().get("href").toString());
                }
            }
            return payment;
        } catch (Exception e) {
            throw new SerializerException("Unable to deserialize initiated payment", e);
        }
    }
}
