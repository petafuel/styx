package net.petafuel.styx.core.xs2a.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.util.ArrayList;

public class DeserialisationHelper {

    private DeserialisationHelper() {
    }

    /**
     * The SCA Object gets modified by reference, the sca approach will be parsed as well as the links map
     *
     * @param strongAuthenticatableResource   this sca Object will be modified by this function
     * @param links a jsonObject which contains the _links object and underlying href -> url key-value pairs
     */
    public static void parseSCALinksData(StrongAuthenticatableResource strongAuthenticatableResource, JsonObject links) {
        if (links.get(LinkType.SCA_REDIRECT.getValue()) != null) {
            strongAuthenticatableResource.getSca().setApproach(SCA.Approach.REDIRECT);
        } else if (links.get(LinkType.SCA_OAUTH.getValue()) != null) {
            strongAuthenticatableResource.getSca().setApproach(SCA.Approach.OAUTH2);
        }
        for (LinkType linkType : LinkType.values()) {
            if (links.get(linkType.getValue()) != null) {
                strongAuthenticatableResource.getLinks().getUrlMapping().put(linkType, new Links.Href(links.get(linkType.getValue()).getAsJsonObject().get("href").getAsString(), linkType));
            }
        }
    }

    public static void parseConsentAccessData(JsonObject consentResponse, Consent target, JsonDeserializationContext context) {
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()) != null) {
            JsonArray balanceAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.BALANCES.value()).getAsJsonArray();
            if(target.getAccess().getBalances() == null){
                target.getAccess().setBalances(new ArrayList<>());
            }
            balanceAccounts.forEach(balanceAccount
                    -> target.getAccess().getBalances().add(context.deserialize(balanceAccount, AccountReference.class)));
        }
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()) != null) {
            JsonArray transactionAccounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.TRANSACTIONS.value()).getAsJsonArray();
            if(target.getAccess().getTransactions() == null){
                target.getAccess().setTransactions(new ArrayList<>());
            }
            transactionAccounts.forEach(transactionAccount
                    -> target.getAccess().getTransactions().add(context.deserialize(transactionAccount, AccountReference.class)));
        }
        if (consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()) != null) {
            JsonArray accounts = consentResponse.get(XS2AJsonKeys.ACCESS.value()).getAsJsonObject().get(XS2AJsonKeys.ACCOUNTS.value()).getAsJsonArray();
            if(target.getAccess().getAccounts() == null){
                target.getAccess().setAccounts(new ArrayList<>());
            }
            accounts.forEach(account
                    -> target.getAccess().getAccounts().add(context.deserialize(account, AccountReference.class)));
        }
    }
}
