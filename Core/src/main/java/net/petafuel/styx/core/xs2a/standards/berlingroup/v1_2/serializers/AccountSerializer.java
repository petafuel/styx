package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Account;

import java.lang.reflect.Type;

public class AccountSerializer implements JsonSerializer<Account> {
    @Override
    public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject encoded = new JsonObject();
        encoded.addProperty(src.getType().getJsonKey(), src.getIdentifier());
        encoded.addProperty("currency", src.getCurrency());
        return encoded;
    }
}
