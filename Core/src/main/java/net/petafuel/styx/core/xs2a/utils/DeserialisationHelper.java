package net.petafuel.styx.core.xs2a.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

public class DeserialisationHelper {

    private DeserialisationHelper() {
    }

    /**
     * The SCA Object gets modified by reference, the sca approach will be parsed as well as the links map
     *
     * @param sca   this sca Object will be modified by this function
     * @param links a jsonObject which contains the _links object and underlying href -> url key-value pairs
     */
    public static void parseSCALinksData(SCA sca, JsonObject links) {
        if (links.get(SCA.LinkType.SCA_REDIRECT.getValue()) != null) {
            sca.setApproach(SCA.Approach.REDIRECT);
        } else if (links.get(SCA.LinkType.SCA_OAUTH.getValue()) != null) {
            sca.setApproach(SCA.Approach.OAUTH2);
        }
        for (SCA.LinkType linkType : SCA.LinkType.values()) {
            if (links.get(linkType.getValue()) != null) {
                sca.addLink(linkType, links.get(linkType.getValue()).getAsJsonObject().get("href").getAsString());
            }
        }
    }

    public static void parseConsentAccessData(JsonObject consentResponse, Consent target, JsonDeserializationContext context) {
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()) != null) {
            JsonArray balanceAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()).getAsJsonArray();
            balanceAccounts.forEach(balanceAccount
                    -> target.getAccess().addBalanceAccounts(context.deserialize(balanceAccount, Account.class)));
        }
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()) != null) {
            JsonArray transactionAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()).getAsJsonArray();
            transactionAccounts.forEach(transactionAccount
                    -> target.getAccess().addTransactionAccounts(context.deserialize(transactionAccount, Account.class)));
        }
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()) != null) {
            JsonArray accounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()).getAsJsonArray();
            accounts.forEach(account
                    -> target.getAccess().addAccounts(context.deserialize(account, Account.class)));
        }
    }
}
