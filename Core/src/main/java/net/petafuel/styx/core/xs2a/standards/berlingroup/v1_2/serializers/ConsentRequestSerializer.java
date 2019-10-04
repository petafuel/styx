package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConsentRequestSerializer implements JsonSerializer<CreateConsentRequest> {

    @Override
    public JsonElement serialize(CreateConsentRequest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject encoded = new JsonObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Account.class, new AccountSerializer())
                .create();

        JsonObject jsonAccess = new JsonObject();

        if (src.getAccess().getBalances() != null && !src.getAccess().getBalances().isEmpty()) {
            JsonElement jsonBalances = gson.toJsonTree(src.getAccess().getBalances(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add("balances", jsonBalances);
        }
        if (src.getAccess().getTransactions() != null && !src.getAccess().getTransactions().isEmpty()) {
            JsonElement jsonTransactions = gson.toJsonTree(src.getAccess().getTransactions(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add("transactions", jsonTransactions);
        }
        encoded.add("access", jsonAccess);

        encoded.addProperty("recurringIndicator", src.isRecurringIndicator());
        encoded.addProperty("validUntil", dateFormat.format(src.getValidUntil()));
        encoded.addProperty("frequencyPerDay", src.getFrequencyPerDay());
        encoded.addProperty("combinedServiceIndicator", src.isCombinedServiceIndicator());
        return encoded;
    }
}
