package net.petafuel.styx.core.xs2a.entities;

public enum XS2AJsonKeys {

    BALANCES("balances"),
    TRANSACTIONS("transactions"),
    ACCESS("access"),
    RECURRING_INDICATOR("recurringIndicator"),
    CONSENT_STATUS("consentStatus"),
    VALID_UNTIL("validUntil"),
    FREQUENCY_PER_DAY("frequencyPerDay"),
    COMBINED_SERVICE_INDICATOR("combinedServiceIndicator"),
    CONSENT_ID("consentId"),
    LINKS("_links"),
    SCA_STATUS("scaStatus"),
    AUTHORISATION_ID("authorisationId"),
    SCA_METHODS("scaMethods"),
    CHOSEN_SCA_METHOD("chosenScaMethod"),
    CHALLENGE("challengeData"),
    PSU_MESSAGE("psuMessage"),
    AUTHENTICATION_TYPE("authenticationType"),
    AUTHENTICATION_VERSION("authenticationVersion"),
    AUTHENTICATION_METHOD_ID("authenticationMethodId"),
    AUTHENTICATION_NAME("name"),
    AUTHENTICATION_EXPLANATION("explanation"),
    CHALLENGE_IMAGE("image"),
    CHALLENGE_DATA("data"),
    CHALLENGE_IMAGE_LINK("imageLink"),
    CHALLENGE_OTP_MAX_LENGTH("optMaxLength"),
    CHALLENGE_OTP_FORMAT("otpFormat"),
    CHALLENGE_ADDITIONAL_INFORMATION("additionalInformation"),
    CURRENCY("currency");

    private String jsonValue;

    XS2AJsonKeys(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String value() {
        return this.jsonValue;
    }
}

