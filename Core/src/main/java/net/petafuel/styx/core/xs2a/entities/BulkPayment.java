package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;
import java.util.List;

public class BulkPayment implements Initializable {
    private List<Payment> payments;
    private Date requestedExecutionDate;
    private Boolean batchBookingPreferred;

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public BulkPayment(List<Payment> payments) {
        this.payments = payments;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public Boolean getBatchBookingPreferred() {
        return batchBookingPreferred;
    }

    public void setBatchBookingPreferred(Boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
    }
}
