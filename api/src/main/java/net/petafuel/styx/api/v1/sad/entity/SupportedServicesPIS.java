package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesPIS {
    @JsonbProperty("singlePayments")
    private boolean singlePayments;

    @JsonbProperty("bulkPayments")
    private boolean bulkPayments;

    @JsonbProperty("periodicPayments")
    private boolean periodicPayments;

    @JsonbProperty("futureDatedPayments")
    private boolean futureDatedPayments;

    public SupportedServicesPIS() {
        // default constructor for json binding
    }

    public SupportedServicesPIS(boolean singlePayments, boolean bulkPayments, boolean periodicPayments, boolean futureDatedPayments) {
        this.singlePayments = singlePayments;
        this.bulkPayments = bulkPayments;
        this.periodicPayments = periodicPayments;
        this.futureDatedPayments = futureDatedPayments;
    }

    public boolean getSinglePayments() {
        return singlePayments;
    }

    public void setSinglePayments(boolean singlePayments) {
        this.singlePayments = singlePayments;
    }

    public boolean getBulkPayments() {
        return bulkPayments;
    }

    public void setBulkPayments(boolean bulkPayments) {
        this.bulkPayments = bulkPayments;
    }

    public boolean getPeriodicPayments() {
        return periodicPayments;
    }

    public void setPeriodicPayments(boolean periodicPayments) {
        this.periodicPayments = periodicPayments;
    }

    public boolean getFutureDatedPayments() {
        return futureDatedPayments;
    }

    public void setFutureDatedPayments(boolean futureDatedPayments) {
        this.futureDatedPayments = futureDatedPayments;
    }
}
