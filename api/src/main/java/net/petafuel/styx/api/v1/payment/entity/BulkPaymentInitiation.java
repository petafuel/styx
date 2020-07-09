package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class BulkPaymentInitiation extends SinglePaymentInitiation {
    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred = false;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    @Valid
    @NotNull
    private Account debtorAccount;

    public Account getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(Account debtorAccount) {
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
}
