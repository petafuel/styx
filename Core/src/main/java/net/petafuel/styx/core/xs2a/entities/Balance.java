package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.BalanceTypeAdapter;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateTimeDeserializer;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Balance implements Serializable {
    private Amount balanceAmount;
    private BalanceType balanceType;
    private Boolean creditLimitIncluded;
    @JsonbTypeDeserializer(ISODateTimeDeserializer.class)
    private Date lastChangeDateTime;
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

    @JsonbTypeAdapter(BalanceTypeAdapter.class)
    public enum BalanceType {
        CLOSING_BOOKED("closingBooked"),
        EXPECTED("expected"),
        OPENING_BOOKED("openingBooked"),
        INTERMIN_AVAILABLE("interimAvailable"),
        INTERMIN_BOOKED("interimBooked"),
        FORWARD_AVAILABLE("forwardAvailable"),
        NON_INVOICED("nonInvoiced");

        private final String jsonValue;

        BalanceType(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static BalanceType getValue(String s) {
            return Arrays.asList(values()).parallelStream().filter(balanceType -> balanceType.jsonValue.equals(s)).findFirst().orElse(null);
        }

        @Override
        public String toString() {
            return jsonValue;
        }
    }
}