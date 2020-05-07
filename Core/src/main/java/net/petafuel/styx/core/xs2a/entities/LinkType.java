package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;

public enum LinkType {
    ACCOUNT("account"),
    AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION("startAuthorisationWithEncryptedPsuAuthentication"),
    AUTHORISATION_WITH_METHOD_SELECTION("startAuthorisationWithAuthenticationMethodSelection"),
    AUTHORISATION_WITH_PSU_AUTHENTICATION("startAuthorisationWithPsuAuthentication"),
    AUTHORISATION_WITH_PSU_IDENTIFICATION("startAuthorisationWithPsuIdentification"),
    AUTHORISATION_WITH_TRANSACTION_AUTHORISATION("startAuthorisationWithTransactionAuthorisation"),
    AUTHORISE_TRANSACTION("authoriseTransaction"),
    AUTHORIZATION_ENDPOINT("authorization_endpoint"),
    BALANCES("balances"),
    CARD_ACCOUNTS("cardAccounts"),
    CARD_TRANSACTIONS("cardTransactions"),
    CONFIRMATION("confirmation"),
    DOWNLOAD("download"),
    FIRST("first"),
    LAST("last"),
    NEXT("next"),
    PREVIOUS("previous"),
    SCA_OAUTH("scaOAuth"),
    SCA_REDIRECT("scaRedirect"),
    SCA_STATUS("scaStatus"),
    SELECT_AUTHENTICATION_METHOD("selectAuthenticationMethod"),
    SELF("self"),
    START_AUTHORISATION("startAuthorisation"), // ASPSP requires to create a authorisation resource
    STATUS("status"),
    TRANSACTIONS("transactions"),
    TRANSACTION_DETAILS("transactionDetails"),
    UPDATE_ADDITIONAL_PSU_AUTHENTICATION("updateAdditionalPsuAuthentication"),
    UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION("updateAdditionalEncryptedPsuAuthentication"),
    UPDATE_ENCRYPTED_PSU_AUTHENTICATION("updateEncryptedPsuAuthentication"),
    UPDATE_PROPRIETARY_DATA("updateProprietaryData"),
    UPDATE_PSU_AUTHENTICATION("updatePsuAuthentication"),
    UPDATE_PSU_IDENTIFICATION("updatePsuIdentification"),
    START_AUTHORISATION_WITH_PROPRIETARY_DATA("startAuthorisationWithProprietaryData");

    private String jsonKey;

    LinkType(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public static LinkType getByString(String search) {
        return Arrays.stream(LinkType.values()).filter(linkType -> linkType.getValue().equals(search)).findFirst().orElse(null);
    }

    public String getValue() {
        return jsonKey;
    }
}
