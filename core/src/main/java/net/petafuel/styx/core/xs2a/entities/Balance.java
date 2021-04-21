package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateTimeDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.io.Serializable;
import java.util.Date;

public class Balance implements Serializable {
    private Amount balanceAmount;
    private BalanceType balanceType;
    private Boolean creditLimitIncluded;

    @JsonbDateFormat("yyyy-MM-dd")
    @JsonbTypeDeserializer(ISODateTimeDeserializer.class)
    private Date lastChangeDateTime;

    @JsonbDateFormat("yyyy-MM-dd")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date referenceDate;

    private String lastCommittedTransaction;

    public Amount getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Amount balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public Boolean getCreditLimitIncluded() {
        return creditLimitIncluded;
    }

    public void setCreditLimitIncluded(Boolean creditLimitIncluded) {
        this.creditLimitIncluded = creditLimitIncluded;
    }

    public Date getLastChangeDateTime() {
        return lastChangeDateTime;
    }

    public void setLastChangeDateTime(Date lastChangeDateTime) {
        this.lastChangeDateTime = lastChangeDateTime;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getLastCommittedTransaction() {
        return lastCommittedTransaction;
    }

    public void setLastCommittedTransaction(String lastCommittedTransaction) {
        this.lastCommittedTransaction = lastCommittedTransaction;
    }

}