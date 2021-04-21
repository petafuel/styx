package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class BulkPaymentInitiation {
    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred = false;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    @NotNull(message = "debtorAccount cannot be null and needs to be outside of the payment object")
    private AccountReference debtorAccount;

    @NotEmpty(message = "Cannot initiate payment without payment objects")
    @JsonbProperty("payments")
    private List<SinglePayment> payments;

    public AccountReference getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(AccountReference debtorAccount) {
        this.debtorAccount = debtorAccount;
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

    public List<SinglePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<SinglePayment> payments) {
        this.payments = payments;
    }
}
