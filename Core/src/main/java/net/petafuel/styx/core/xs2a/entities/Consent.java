package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.*;

public class Consent {

    // Identifier for Persistence (database ID)
    private UUID id;
    // Identifier received from Bank (consent ID)
    private String consentId;
    private XS2ARequest request;
    private State state;
    private boolean recurringIndicator;
    private Date validUntil;
    private Date lastActionDate;
    private int frequencyPerDay;
    private SCA sca;
    private Access access;

    public Consent() {
        this.sca = new SCA();
        this.access = new Access();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public XS2ARequest getRequest() {
        return request;
    }

    public void setRequest(XS2ARequest request) {
        this.request = request;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isRecurringIndicator() {
        return recurringIndicator;
    }

    public void setRecurringIndicator(boolean recurringIndicator) {
        this.recurringIndicator = recurringIndicator;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public Date getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(Date lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public int getFrequencyPerDay() {
        return frequencyPerDay;
    }

    public void setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public SCA getSca() {
        return sca;
    }

    public void setSca(SCA sca) {
        this.sca = sca;
    }

    public Access getAccess() {
        return access;
    }

    public enum State {
        //consent received, not authorised yet
        RECEIVED("received"),
        //consent rejected, authorisation was not successful
        REJECTED("rejected"),
        //consent "in progress", not all required authrisation steps have been completed
        PARTIALLY_AUTHORISED("partiallyAuthorised"),
        //consent is ready to be used
        VALID("valid"),
        //the psu has revoked the consent towards the aspsp
        REVOKED_BY_PSU("revokedByPsu"),
        //consent validity has expired
        EXPIRED("expired"),
        //consent was terminated due to DELETE /consents/{consentid} call
        TERMINATED_BY_TPP("terminatedByTpp");

        private String jsonKey;

        State(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getJsonKey() {
            return jsonKey;
        }

        public static Consent.State getByString(String search) {
            return Arrays.stream(Consent.State.values()).filter(linkType -> linkType.getJsonKey().equals(search)).findFirst().orElse(null);
        }
    }
}
