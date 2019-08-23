package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.*;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;
import java.util.Arrays;

public class AccountSerializer implements JsonSerializer<Account>, JsonDeserializer<Account> {

    private static final String CURRENCY = "currency";

    @Override
    public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject encoded = new JsonObject();
        encoded.addProperty(src.getType().getJsonKey(), src.getIdentifier());
        encoded.addProperty(CURRENCY, src.getCurrency());
        return encoded;
    }

    @Override
    public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        Account.Type accountType = Arrays.stream(Account.Type.values())
                .filter(type -> jsonObject.get(type.getJsonKey()) != null)
                .findFirst()
                .orElse(null);
        if(accountType == null)
        {
            throw new SerializerException("Unable to deserialize the Account Object: accountType did not match any Account.Type");
        }

        return new Account(jsonObject.get(accountType.getJsonKey()).getAsString(),
                (jsonObject.get(CURRENCY) != null ? jsonObject.get(CURRENCY).getAsString() : null),
                accountType);
    }
}
