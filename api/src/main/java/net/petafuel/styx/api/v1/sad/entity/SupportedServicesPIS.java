package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesPIS {
    @JsonbProperty("singlePayments")
    private Boolean singlePayments;

    @JsonbProperty("bulkPayments")
    private Boolean bulkPayments;

    @JsonbProperty("periodicPayments")
    private Boolean periodicPayments;

    @JsonbProperty("futureDatedPayments")
    private Boolean futureDatedPayments;

    public SupportedServicesPIS() {
        // default constructor for json binding
    }

    public SupportedServicesPIS(Boolean singlePayments, Boolean bulkPayments, Boolean periodicPayments, Boolean futureDatedPayments) {
        this.singlePayments = singlePayments;
        this.bulkPayments = bulkPayments;
        this.periodicPayments = periodicPayments;
        this.futureDatedPayments = futureDatedPayments;
    }

    public Boolean getSinglePayments() {
        return singlePayments;
    }

    public void setSinglePayments(Boolean singlePayments) {
        this.singlePayments = singlePayments;
    }

    public Boolean getBulkPayments() {
        return bulkPayments;
    }

    public void setBulkPayments(Boolean bulkPayments) {
        this.bulkPayments = bulkPayments;
    }

    public Boolean getPeriodicPayments() {
        return periodicPayments;
    }

    public void setPeriodicPayments(Boolean periodicPayments) {
        this.periodicPayments = periodicPayments;
    }

    public Boolean getFutureDatedPayments() {
        return futureDatedPayments;
    }

    public void setFutureDatedPayments(Boolean futureDatedPayments) {
        this.futureDatedPayments = futureDatedPayments;
    }
}
