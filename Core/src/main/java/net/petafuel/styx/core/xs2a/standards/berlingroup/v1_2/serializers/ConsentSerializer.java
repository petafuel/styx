package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConsentSerializer implements JsonDeserializer<Consent>, JsonSerializer<CreateConsentRequest> {

    private static final String JSON_KEY_BALANCES = "balances";
    private static final String JSON_KEY_TRANSACTIONS = "transactions";
    private static final String JSON_KEY_ACCESS = "access";
    private static final String JSON_KEY_RECURRING_INDICATOR = "recurringIndicator";
    private static final String JSON_KEY_CONSENT_STATUS = "consentStatus";
    private static final String JSON_KEY_VALID_UTIL = "validUntil";
    private static final String JSON_KEY_FREQUENCY_PER_DAY = "frequencyPerDay";
    private static final String JSON_KEY_COMBINED_SERVICE_INDICATOR = "combinedServiceIndicator";
    private static final String JSON_KEY_CONSENT_ID = "consentId";

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
            jsonAccess.add(JSON_KEY_BALANCES, jsonBalances);
        }
        if (src.getAccess().getTransactions() != null && !src.getAccess().getTransactions().isEmpty()) {
            JsonElement jsonTransactions = gson.toJsonTree(src.getAccess().getTransactions(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add(JSON_KEY_TRANSACTIONS, jsonTransactions);
        }
        encoded.add(JSON_KEY_ACCESS, jsonAccess);

        encoded.addProperty(JSON_KEY_RECURRING_INDICATOR, src.isRecurringIndicator());
        encoded.addProperty(JSON_KEY_VALID_UTIL, dateFormat.format(src.getValidUntil()));
        encoded.addProperty(JSON_KEY_FREQUENCY_PER_DAY, src.getFrequencyPerDay());
        encoded.addProperty(JSON_KEY_COMBINED_SERVICE_INDICATOR, src.isCombinedServiceIndicator());
        return encoded;
    }

    @Override
    public Consent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject consentResponse = json.getAsJsonObject();
        Consent consent = new Consent();
        if (consentResponse.get(JSON_KEY_CONSENT_ID) != null) {
            consent.setConsentId(consentResponse.get(JSON_KEY_CONSENT_ID).getAsString());
        }
        if (consentResponse.get(JSON_KEY_CONSENT_STATUS) != null) {
            consent.setState(Consent.State.valueOf(consentResponse.get(JSON_KEY_CONSENT_STATUS).getAsString().toUpperCase()));
        }

        if (consentResponse.get(JSON_KEY_RECURRING_INDICATOR) != null) {
            consent.setRecurringIndicator(consentResponse.get(JSON_KEY_RECURRING_INDICATOR).getAsBoolean());
        }
        if (consentResponse.get(JSON_KEY_VALID_UTIL) != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                consent.setValidUntil(simpleDateFormat.parse(consentResponse.get(JSON_KEY_VALID_UTIL).getAsString()));
            } catch (ParseException e) {
                throw new SerializerException("Unable to deserialize Consent validUntil to Date format: " + e.getMessage());
            }
        }
        if (consentResponse.get(JSON_KEY_FREQUENCY_PER_DAY) != null) {
            consent.setFrequencyPerDay(consentResponse.get(JSON_KEY_FREQUENCY_PER_DAY).getAsInt());
        }
        if (consentResponse.get(JSON_KEY_CONSENT_STATUS) != null) {
            consent.setState(Consent.State.getByString(json.getAsJsonObject().get(JSON_KEY_CONSENT_STATUS).getAsString()));
        }
        if (consentResponse.get(JSON_KEY_ACCESS) != null) {
            if (consentResponse.get(JSON_KEY_ACCESS).getAsJsonObject().get(JSON_KEY_BALANCES) != null) {
                JsonArray balanceAccounts = consentResponse.get(JSON_KEY_ACCESS).getAsJsonObject().get(JSON_KEY_BALANCES).getAsJsonArray();
                consent.getAccess().addBalanceAccounts(context.deserialize(balanceAccounts, Account.class));

            }
            if (consentResponse.get(JSON_KEY_ACCESS).getAsJsonObject().get(JSON_KEY_TRANSACTIONS) != null) {
                JsonArray transactionAccounts = consentResponse.get(JSON_KEY_ACCESS).getAsJsonObject().get(JSON_KEY_TRANSACTIONS).getAsJsonArray();
                consent.getAccess().addTransactionAccounts(context.deserialize(transactionAccounts, Account.class));
            }
        }


        return consent;
    }
}
