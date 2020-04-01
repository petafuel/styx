package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import net.petafuel.styx.core.xs2a.entities.Consent;

import java.lang.reflect.Type;

public class ConsentStatusSerializer implements JsonDeserializer<Consent.State> {
    @Override
    public Consent.State deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return Consent.State.getByString(json.getAsJsonObject().get("consentStatus").getAsString());
    }
}
