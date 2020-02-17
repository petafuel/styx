package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Date;
import java.util.List;

public class BulkPayment implements InitializablePayment {
    @JsonbProperty("debtorAccount")
    Account debtorAccount;

    @JsonbProperty("payments")
    @JsonbTypeAdapter(BulkPaymentAdapter.class)
    List<Payment> payments;

    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date requestedExecutionDate;

    public Account getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(Account debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
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
