package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;

import java.lang.reflect.Type;

public class AccountSerializer implements JsonDeserializer<Account> {
    @Override
    public Account deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject accountJsonObject = jsonElement.getAsJsonObject();
        Currency currency;
        if (accountJsonObject.get("currency") == null) {
            currency = Currency.EUR;
        } else {
            currency = Currency.valueOf(accountJsonObject.get("currency").getAsString());
        }
        Account.Type accountType;
        String identifier;

        if (!accountJsonObject.get("iban").isJsonNull()) {
            accountType = Account.Type.IBAN;
            identifier = accountJsonObject.get("iban").getAsString();
        } else if (!accountJsonObject.get("bban").isJsonNull()) {
            accountType = Account.Type.BBAN;
            identifier = accountJsonObject.get("bban").getAsString();
        } else if (!accountJsonObject.get("pan").isJsonNull()) {
            accountType = Account.Type.PAN;
            identifier = accountJsonObject.get("pan").getAsString();
        } else if (!accountJsonObject.get("maskedPan").isJsonNull()) {
            accountType = Account.Type.MASKED_PAN;
            identifier = accountJsonObject.get("maskedPan").getAsString();
        } else {
            accountType = Account.Type.MSISDN;
            identifier = accountJsonObject.get("msisdn").getAsString();
        }

        Account account = new Account(identifier, currency, accountType);
        if (accountJsonObject.has("name")) {
            account.setName(accountJsonObject.get("name").getAsString());
        }

        return account;
    }
}
