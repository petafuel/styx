package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesCOF {
    @JsonbProperty("fundsConfirmation")
    private Boolean fundsConfirmation;

    public SupportedServicesCOF() {
        // default constrfuctor for json binding
    }

    public SupportedServicesCOF(Boolean fundsConfirmation) {
        this.fundsConfirmation = fundsConfirmation;
    }

    public Boolean getFundsConfirmation() {
        return fundsConfirmation;
    }

    public void setFundsConfirmation(Boolean fundsConfirmation) {
        this.fundsConfirmation = fundsConfirmation;
    }
}
