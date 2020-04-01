package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.api.validator.ValidateExecutionRule;
import net.petafuel.styx.api.validator.ValidateFrequency;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class PeriodicPaymentInitiation extends SinglePaymentInitiation {
    @JsonbDateFormat(value = "yyyy-MM-dd")
    @JsonbProperty("startDate")
    @NotNull(message = "startDate cannot be null for periodic payments")
    private LocalDate startDate;

    @NotNull(message = "executionRule cannot be null for periodic payments")
    @JsonbProperty("executionRule")
    @ValidateExecutionRule(message = "requires valid ExecutionRule")
    private String executionRule;

    @JsonbProperty("frequency")
    @NotNull(message = "frequency cannot be null for periodic payments")
    @ValidateFrequency(message = "requires valid Frequency code")
    private String frequency;

    @JsonbProperty("dayOfExecution")
    @NotNull(message = "dayOfExecution cannot be null for periodic payments")
    @Max(31)
    @Min(1)
    private Integer dayOfExecution;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public PeriodicPayment.ExecutionRule getExecutionRule() {
        return PeriodicPayment.ExecutionRule.valueOf(executionRule);
    }

    public void setExecutionRule(String executionRule) {
        this.executionRule = executionRule.toUpperCase();
    }

    public PeriodicPayment.Frequency getFrequency() {
        return PeriodicPayment.Frequency.valueOf(frequency);
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency.toUpperCase();
    }

    public Integer getDayOfExecution() {
        return dayOfExecution;
    }

    public void setDayOfExecution(Integer dayOfExecution) {
        this.dayOfExecution = dayOfExecution;
    }
}
