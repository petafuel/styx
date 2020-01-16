package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.utils.DeserialisationHelper;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConsentSerializer implements JsonDeserializer<Consent>, JsonSerializer<CreateConsentRequest> {
    @Override
    public JsonElement serialize(CreateConsentRequest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject encoded = new JsonObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Account.class, new AccountSerializer())
                .create();

        JsonObject jsonAccess = new JsonObject();

        if (src.getConsent().getAccess().getBalances() != null && !src.getConsent().getAccess().getBalances().isEmpty()) {
            JsonElement jsonBalances = gson.toJsonTree(src.getConsent().getAccess().getBalances(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add(XS2AJsonKeys.BALANCES.value(), jsonBalances);
        }

        if (src.getConsent().getAccess().getAccounts() != null && !src.getConsent().getAccess().getAccounts().isEmpty()) {
            JsonElement jsonBalances = gson.toJsonTree(src.getConsent().getAccess().getAccounts(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add(XS2AJsonKeys.ACCOUNTS.value(), jsonBalances);
        }

        if (src.getConsent().getAccess().getTransactions() != null && !src.getConsent().getAccess().getTransactions().isEmpty()) {
            JsonElement jsonTransactions = gson.toJsonTree(src.getConsent().getAccess().getTransactions(), new TypeToken<ArrayList<Account>>() {
            }.getType());
            jsonAccess.add(XS2AJsonKeys.TRANSACTIONS.value(), jsonTransactions);
        }
        encoded.add(XS2AJsonKeys.ACCESS.value(), jsonAccess);

        encoded.addProperty(XS2AJsonKeys.RECURRING_INDICATOR.value(), src.getConsent().isRecurringIndicator());
        encoded.addProperty(XS2AJsonKeys.VALID_UNTIL.value(), dateFormat.format(src.getConsent().getValidUntil()));
        encoded.addProperty(XS2AJsonKeys.FREQUENCY_PER_DAY.value(), src.getConsent().getFrequencyPerDay());
        encoded.addProperty(XS2AJsonKeys.COMBINED_SERVICE_INDICATOR.value(), src.getConsent().isCombinedServiceIndicator());
        return encoded;
    }

    @Override
    public Consent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject consentResponse = json.getAsJsonObject();
        Consent consent = new Consent();
        if (consentResponse.get(XS2AJsonKeys.CONSENT_ID.value()) != null) {
            consent.setId(consentResponse.get(XS2AJsonKeys.CONSENT_ID.value()).getAsString());
        }
        if (consentResponse.get(XS2AJsonKeys.CONSENT_STATUS.value()) != null) {
            consent.setState(Consent.State.valueOf(consentResponse.get(XS2AJsonKeys.CONSENT_STATUS.value()).getAsString().toUpperCase()));
        }

        if (consentResponse.get(XS2AJsonKeys.RECURRING_INDICATOR.value()) != null) {
            consent.setRecurringIndicator(consentResponse.get(XS2AJsonKeys.RECURRING_INDICATOR.value()).getAsBoolean());
        }
        if (consentResponse.get(XS2AJsonKeys.VALID_UNTIL.value()) != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                consent.setValidUntil(simpleDateFormat.parse(consentResponse.get(XS2AJsonKeys.VALID_UNTIL.value()).getAsString()));
            } catch (ParseException e) {
                throw new SerializerException("Unable to deserialize Consent validUntil to Date format: " + e.getMessage());
            }
        }
        if (consentResponse.get(XS2AJsonKeys.FREQUENCY_PER_DAY.value()) != null) {
            consent.setFrequencyPerDay(consentResponse.get(XS2AJsonKeys.FREQUENCY_PER_DAY.value()).getAsInt());
        }
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()) != null) {
            if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()) != null) {
                JsonArray balanceAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()).getAsJsonArray();
                balanceAccounts.forEach(balanceAccount
                        -> consent.getAccess().addBalanceAccounts(context.deserialize(balanceAccount, Account.class)));
            }
            if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()) != null) {
                JsonArray transactionAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()).getAsJsonArray();
                transactionAccounts.forEach(transactionAccount
                        -> consent.getAccess().addTransactionAccounts(context.deserialize(transactionAccount, Account.class)));
            }
            if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()) != null) {
                JsonArray accounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()).getAsJsonArray();
                accounts.forEach(account
                        -> consent.getAccess().addAccounts(context.deserialize(account, Account.class)));
            }
        }
        if (consentResponse.get(XS2AJsonKeys.LINKS.value()) != null && !consentResponse.get(XS2AJsonKeys.LINKS.value()).isJsonNull()) {
            JsonObject links = consentResponse.get(XS2AJsonKeys.LINKS.value()).getAsJsonObject();
            DeserialisationHelper.parseSCALinksData(consent.getSca(), links);
        }

        return consent;
    }
}
