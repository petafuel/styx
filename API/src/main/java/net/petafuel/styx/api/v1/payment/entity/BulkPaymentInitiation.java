package net.petafuel.styx.api.v1.payment.entity;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import java.time.LocalDate;

public class BulkPaymentInitiation extends PaymentInitiation {

    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred = false;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    private LocalDate requestedExecutionDate;

    public BulkPaymentInitiation() {
        //json bind constructor
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
