package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class PeriodicPayment extends Payment {

    private Date startDate;
    private ExecutionRule executionRule;
    private Date endDate;
    private String frequency;

    private String dayOfExecution;

    public PeriodicPayment() {}

    public PeriodicPayment(Date startDate, String frequency) {
        this.startDate = startDate;
        this.frequency = frequency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * day of execution depending on frequency
     * <br>should be explicit day in month (twoDigitText) on which periodic payment should be executed
     * <br>f.e. frequency = "MNTH" and dayOfExecution = "20" - payment is executed every month on the 20th day
     * @see https://www.iso20022.org/standardsrepository/public/wqt/Description/mx/dico/codesets/_VsZaMf70EeCKvdA5_Kg7Aw
     * @return String
     */
    public String getDayOfExecution() {
        return dayOfExecution;
    }

    /**
     * day of execution depending on frequency
     * <br>should be explicit day in month (twoDigitText) on which periodic payment should be executed
     * <br>f.e. frequency = "MNTH" and dayOfExecution = "20" - payment is executed every month on the 20th day
     * @see https://www.iso20022.org/standardsrepository/public/wqt/Description/mx/dico/codesets/_VsZaMf70EeCKvdA5_Kg7Aw
     * @param dayOfExecution String
     */
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
