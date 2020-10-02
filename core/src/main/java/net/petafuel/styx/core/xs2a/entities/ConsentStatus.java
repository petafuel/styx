package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;

public enum ConsentStatus {
    //consent received, not authorised yet
    RECEIVED(1, "received"),
    //consent rejected, authorisation was not successful
    REJECTED(2, "rejected"),
    //consent "in progress", not all required authrisation steps have been completed
    PARTIALLY_AUTHORISED(3, "partiallyAuthorised"),
    //consent is ready to be used
    VALID(4, "valid"),
    //the psu has revoked the consent towards the aspsp
    REVOKEDBYPSU(5, "revokedByPsu"),
    //consent validity has expired
    EXPIRED(6, "expired"),
    //consent was terminated due to DELETE /consents/{consentid} call
    TERMINATED_BY_TPP(7, "terminatedByTpp");

    private String jsonKey;
    private int index;

    ConsentStatus(int index, String jsonKey) {
        this.index = index;
        this.jsonKey = jsonKey;
    }

    public static ConsentStatus getByString(String search) {
        return Arrays.stream(ConsentStatus.values()).filter(linkType -> linkType.getJsonKey().equals(search)).findFirst().orElse(null);
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public int getIndex() {
        return index;
    }
}