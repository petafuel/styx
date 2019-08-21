package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;

import java.lang.reflect.Type;
import java.util.Map;

public class ConsentSerializer implements JsonDeserializer<Consent> {
    @Override
    public Consent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject consentResponse = json.getAsJsonObject();

        Consent consent = new Consent();
        consent.setConsentId(consentResponse.get("consentId").getAsString());
        consent.setState(Consent.State.valueOf(consentResponse.get("consentStatus").getAsString().toUpperCase()));

        SCA sca = new SCA();
        JsonObject links = consentResponse.get("_links").getAsJsonObject();
        for (Map.Entry<String, JsonElement> link : links.entrySet()) {
            sca.addLink(SCA.LinkType.getByString(link.getKey()), link.getValue().getAsJsonObject().get("href").getAsString());
        }

        consent.setSca(sca);
        return consent;
    }
}
