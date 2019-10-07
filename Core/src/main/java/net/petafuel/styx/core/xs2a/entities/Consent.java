package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.*;

public class Consent {

    // Identifier for Persistence (database ID)
    private String id;
    private XS2ARequest request;
    private State state;
    private boolean recurringIndicator;
    private boolean combinedServiceIndicator;
    private Date validUntil;
    private Date lastUpdated;
    private int frequencyPerDay;
    private SCA sca;
    private Access access;
    private PSU psu;
    public Consent() {
        this.sca = new SCA();
        this.access = new Access();
        Calendar calendar = Calendar.getInstance();
        calendar.set(9999, 1, 1);
        this.validUntil = calendar.getTime();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
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
    public boolean isCombinedServiceIndicator() {
        return combinedServiceIndicator;
    }
    public void setCombinedServiceIndicator(boolean combinedServiceIndicator) {
        this.combinedServiceIndicator = combinedServiceIndicator;
    }
    public PSU getPsu() {
        return psu;
    }
    public void setPsu(PSU psu) {
        this.psu = psu;
    }
    public enum State {
        //consent received, not authorised yet
        RECEIVED(1, "received"),
        //consent rejected, authorisation was not successful
        REJECTED(2, "rejected"),
        //consent "in progress", not all required authrisation steps have been completed
        PARTIALLY_AUTHORISED(3, "partiallyAuthorised"),
        //consent is ready to be used
        VALID(4, "valid"),
        //the psu has revoked the consent towards the aspsp
        REVOKED_BY_PSU(5,"revokedByPsu"),
        //consent validity has expired
        EXPIRED(6,"expired"),
        //consent was terminated due to DELETE /consents/{consentid} call
        TERMINATED_BY_TPP(7,"terminatedByTpp");
        private String jsonKey;
        private int index;
        State(int index, String jsonKey) {
            this.index = index;
            this.jsonKey = jsonKey;
        }
        public String getJsonKey() {
            return jsonKey;
        }
        public int getIndex() {
            return index;
        }
        public static Consent.State getByString(String search) {
            return Arrays.stream(Consent.State.values()).filter(linkType -> linkType.getJsonKey().equals(search)).findFirst().orElse(null);
        }
    }
}