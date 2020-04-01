package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountSerializer implements JsonSerializer<Account>, JsonDeserializer<List<Account>> {

    @Override
    public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject encoded = new JsonObject();
        encoded.addProperty(src.getType().getJsonKey(), src.getIdentifier());
        encoded.addProperty(XS2AJsonKeys.CURRENCY.value(), src.getCurrency().toString());
        return encoded;
    }

    @Override
    public List<Account> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json.isJsonObject()) {
            JsonObject possibleAccount = json.getAsJsonObject();
            if (possibleAccount.has("account")) {
                return this.deserializeSingleAccount(possibleAccount.getAsJsonObject("account"));
            } else if (possibleAccount.has("accounts")) {
                return this.deserializeMultipleAccount(possibleAccount.getAsJsonArray("accounts"));
            } else {
                return this.deserializeSingleAccount(json.getAsJsonObject());
            }
        } else {
            return this.deserializeMultipleAccount(json.getAsJsonArray());
        }
    }

    private List<Account> deserializeSingleAccount(JsonObject object) {

        ArrayList<Account> list = new ArrayList<>();
        list.add(this.mapToModel(object));
        return list;
    }

    private List<Account> deserializeMultipleAccount(JsonArray array) {

        ArrayList<Account> list = new ArrayList<>();

        for (JsonElement element : array) {
            JsonObject object = (JsonObject) element;
            list.add(this.mapToModel(object));
        }
        return list;
    }

    private Account mapToModel(JsonObject object) {
        String resourceId = null;
        String cashAccountType = null;
        String product = null;
        String name = null;
        Account.Type accountType = Arrays.stream(Account.Type.values())
                .filter(type -> object.get(type.getJsonKey()) != null)
                .findFirst()
                .orElse(null);
        if (accountType == null) {
            throw new SerializerException("Unable to deserialize the Account Object: accountType did not match any Account.Type");
        }
        String identifier = object.get(accountType.getJsonKey()).getAsString();

        if (object.get(XS2AJsonKeys.CURRENCY.value()) == null) {
            throw new SerializerException("Unable to deserialize the Account Object: Attribute currency not given");
        }
        Currency currency = Currency.valueOf(object.get(XS2AJsonKeys.CURRENCY.value()).getAsString().toUpperCase());

        if (object.get("resourceId") != null) {
            resourceId = object.get("resourceId").getAsString();
        }
        if (object.get("cashAccountType") != null) {
            cashAccountType = object.get("cashAccountType").getAsString();
        }
        if (object.get("product") != null) {
            product = object.get("product").getAsString();
        }
        if (object.get("name") != null) {
            name = object.get("name").getAsString();
        }

        Account account = new Account(identifier, currency, accountType);
        account.setResourceId(resourceId);
        account.setName(name);
        account.setCashAccountType(cashAccountType);
        account.setProduct(product);

        if (object.get(XS2AJsonKeys.BALANCES.value()) != null && !object.get(XS2AJsonKeys.BALANCES.value()).isJsonNull()) {
            BalancesSerializer balancesSerializer = new BalancesSerializer();
            JsonArray balances = object.get(XS2AJsonKeys.BALANCES.value()).getAsJsonArray();
            for (JsonElement element : balances) {
                account.addBalance(balancesSerializer.mapToModel((JsonObject) element));
            }
        }
        return account;
    }
}
