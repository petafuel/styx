package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Calendar;
import java.util.Date;

/**
 * A basic consent model as created in AIS context or in combined service context with AIS and PIS
 */
public class Consent extends StrongAuthenticatableResource {

    @JsonbProperty("consentId")
    private String id;

    private int frequencyPerDay;
    private boolean recurringIndicator;
    private boolean combinedServiceIndicator;

    @JsonbTypeDeserializer(ISODateDeserializer.class)
    @JsonbDateFormat("yyyy-MM-dd")
    private Date validUntil;

    @JsonbProperty("lastActionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    @JsonbDateFormat("yyyy-MM-dd")
    private Date lastAction;

    @JsonbTransient
    private Date lastUpdated;

    @JsonbTransient
    private Date createdAt;
    private AccountAccess access;
    @JsonbTransient
    private PSU psu;
    @JsonbTransient
    private ConsentStatus state;

    public Consent() {
        this.sca = new SCA();
        this.access = new AccountAccess();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 90);
        this.validUntil = calendar.getTime();
    }

    @JsonbCreator
    public Consent(@JsonbProperty("consentStatus") String consentStatus) {
        this();
        this.state = ConsentStatus.getByString(consentStatus);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("consentStatus")
    public String getStateJson() {
        return state != null ? state.getJsonKey() : null;
    }

    public ConsentStatus getState() {
        return state;
    }

    public void setState(ConsentStatus state) {
        this.state = state;
    }

    public boolean isRecurringIndicator() {
        return recurringIndicator;
    }

    public void setRecurringIndicator(boolean recurringIndicator) {
        this.recurringIndicator = recurringIndicator;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getFrequencyPerDay() {
        return frequencyPerDay;
    }

    public void setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public AccountAccess getAccess() {
        return access;
    }

    public void setAccess(AccountAccess access) {
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

}