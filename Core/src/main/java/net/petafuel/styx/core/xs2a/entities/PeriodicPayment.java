package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class PeriodicPayment extends Payment {

    private Date startDate;
    private ExecutionRule executionRule;
    private Date endDate;
    private Frequency frequency;
    private String dayOfExecution;

    public PeriodicPayment(Date startDate, Frequency frequency) {
        this.startDate = startDate;
        this.frequency = frequency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ExecutionRule getExecutionRule() {
        return executionRule;
    }

    public void setExecutionRule(ExecutionRule executionRule) {
        this.executionRule = executionRule;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public String getDayOfExecution() {
        return dayOfExecution;
    }

    public void setDayOfExecution(String dayOfExecution) {
        this.dayOfExecution = dayOfExecution;
    }

    public enum ExecutionRule {
        following,
        preceding
    }

    public enum Frequency {
        YEAR("Annual"),
        SEMI("SemiAnnual"),
        QUTR("Quarterly"),
        TOMN("EveryTwoMonths"),
        MNTH("Monthly"),
        TWMW("TwiceAMonth"),
        TOWK("EveryTwoWeeks"),
        WEEK("Weekly"),
        DAIL("Daily"),
        ADHO("Adhoc"),
        INDA("IntraDay"),
        OVNG("Overnight"),
        ONDE("OnDemand");

        private String name;
        Frequency(String fullName) {
            this.name = fullName;
        }

        public String getName() {
            return name;
        }
    }
}
