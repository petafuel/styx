package net.petafuel.styx.core.xs2a.entities;

import java.time.LocalDate;
import java.util.List;

public class BulkPayment implements InitializablePayment {
    Account debtorAccount;
    List<Payment> payments;
    private Boolean batchBookingPreferred;
    private LocalDate requestedExecutionDate;

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

    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
}
