package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateTimeDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Date;
import java.util.List;

public class BulkPayment implements InitializablePayment {
    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred;

    @JsonbProperty("debtorAccount")
    private AccountReference debtorAccount;

    @JsonbProperty("paymentInformationId")
    private String paymentInformationId;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionTime")
    @JsonbTypeDeserializer(ISODateTimeDeserializer.class)
    private Date requestedExecutionTime;

    @JsonbProperty("payments")
    @JsonbTypeAdapter(BulkPaymentAdapter.class)
    List<SinglePayment> payments;


    public AccountReference getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(AccountReference debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public List<SinglePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<SinglePayment> payments) {
        this.payments = payments;
    }

    public Boolean getBatchBookingPreferred() {
        return batchBookingPreferred;
    }

    public void setBatchBookingPreferred(Boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
}
