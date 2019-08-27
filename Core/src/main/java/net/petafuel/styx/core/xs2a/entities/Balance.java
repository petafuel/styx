package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class Balance {

    private Date lastChangeDateTime;

    private Currency currency;

    private float amount;

    private Type type;

    private Date referenceDate;

    public Balance(float amount, Type type, Currency currency) {
        this.amount = amount;
        this.type = type;
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Date getLastChangeDateTime() {
        return lastChangeDateTime;
    }

    public void setLastChangeDateTime(Date lastChangeDateTime) {
        this.lastChangeDateTime = lastChangeDateTime;
    }

    public enum Type {
        CLOSING_BOOKED,
        EXPECTED
    }
}
