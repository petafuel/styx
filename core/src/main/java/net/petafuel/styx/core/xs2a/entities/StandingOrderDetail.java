package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.ISODateDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Date;

public class StandingOrderDetail {
    @JsonbDateFormat("yyyy-MM-dd")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date startDate;

    @JsonbDateFormat("yyyy-MM-dd")
    @JsonbTypeDeserializer(ISODateDeserializer.class)
    private Date endDate;

    private String executionRule;
    private Boolean withinAMonthFlag;
    private Frequency frequency;
    private String[] monthsOfExecution;
    private Float multiplicator;
    private String dayOfExecution;
    private Amount limitAmount;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getExecutionRule() {
        return executionRule;
    }

    public void setExecutionRule(String executionRule) {
        this.executionRule = executionRule;
    }

    public Boolean getWithinAMonthFlag() {
        return withinAMonthFlag;
    }

    public void setWithinAMonthFlag(Boolean withinAMonthFlag) {
        this.withinAMonthFlag = withinAMonthFlag;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public String[] getMonthsOfExecution() {
        return monthsOfExecution;
    }

    public void setMonthsOfExecution(String[] monthsOfExecution) {
        this.monthsOfExecution = monthsOfExecution;
    }

    public Float getMultiplicator() {
        return multiplicator;
    }

    public void setMultiplicator(Float multiplicator) {
        this.multiplicator = multiplicator;
    }

    public String getDayOfExecution() {
        return dayOfExecution;
    }

    public void setDayOfExecution(String dayOfExecution) {
        this.dayOfExecution = dayOfExecution;
    }

    public Amount getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(Amount limitAmount) {
        this.limitAmount = limitAmount;
    }
}