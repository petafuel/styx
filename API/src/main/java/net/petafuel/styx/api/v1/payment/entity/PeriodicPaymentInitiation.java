package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class PeriodicPaymentInitiation extends PaymentInitiation {

    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("startDate")
    @NotNull
    private LocalDate startDate;

    @JsonbProperty("executionRule")
    @NotNull
    private PeriodicPayment.ExecutionRule executionRule;

    @JsonbProperty("frequency")
    @NotNull
    private String frequency;

    @JsonbProperty("dayOfExecution")
    @NotNull
    private Integer dayOfExecution;

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
}
