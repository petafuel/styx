package net.petafuel.styx.api.v1.payment.boundary.entity;

import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

public class PaymentInitiationRequest {
    @JsonbProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("requestedExecutionDate")
    private LocalDate requestedExecutionDate;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("startDate")
    private LocalDate startDate;

    @JsonbProperty("executionRule")
    private PeriodicPayment.ExecutionRule executionRule;

    @JsonbProperty("frequency")
    private String frequency;

    @JsonbProperty("dayOfExecution")
    private Integer dayOfExecution;

    @NotEmpty(message = "Cannot initiate payment without payment objects")
    @JsonbProperty("payments")
    @Valid
    private List<Payment> payments;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public PeriodicPayment.ExecutionRule getExecutionRule() {
        return executionRule;
    }

    public void setExecutionRule(PeriodicPayment.ExecutionRule executionRule) {
        this.executionRule = executionRule;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDayOfExecution() {
        return dayOfExecution;
    }

    public void setDayOfExecution(Integer dayOfExecution) {
        this.dayOfExecution = dayOfExecution;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
