package net.petafuel.styx.core.xs2a.entities;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * A basic consent model as created in AIS conext or in combined service context with AIS and PIS
 */
public class Consent {

    private String id;
    private UUID xRequestId;
    private int frequencyPerDay;
    private boolean recurringIndicator;
    private boolean combinedServiceIndicator;
    private Date validUntil;
    private Date lastUpdated;
    private Date createdAt;
    private SCA sca;
    private Access access;
    private PSU psu;
    private State state;

    public Consent() {
        this.sca = new SCA();
        this.access = new Access();
        Calendar calendar = Calendar.getInstance();
        calendar.set(9999, Calendar.JANUARY, 1);
        this.validUntil = calendar.getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(UUID xRequestId) {
        this.xRequestId = xRequestId;
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
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
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

    public void setAccess(Access access) {
        this.access = access;
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
        REVOKED_BY_PSU(5, "revokedByPsu"),
        //consent validity has expired
        EXPIRED(6, "expired"),
        //consent was terminated due to DELETE /consents/{consentid} call
        TERMINATED_BY_TPP(7, "terminatedByTpp");

        private String jsonKey;
        private int index;

        State(int index, String jsonKey) {
            this.index = index;
            this.jsonKey = jsonKey;
        }

        public static Consent.State getByString(String search) {
            return Arrays.stream(Consent.State.values()).filter(linkType -> linkType.getJsonKey().equals(search)).findFirst().orElse(null);
        }

        public String getJsonKey() {
            return jsonKey;
        }

        public int getIndex() {
            return index;
        }
    }
}