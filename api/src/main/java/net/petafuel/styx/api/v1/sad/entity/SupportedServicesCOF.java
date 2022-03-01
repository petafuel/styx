package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class SupportedServicesCOF {
    @JsonbProperty("fundsConfirmation")
    private boolean fundsConfirmation;

    public SupportedServicesCOF() {
        // default constrfuctor for json binding
    }

    public SupportedServicesCOF(boolean fundsConfirmation) {
        this.fundsConfirmation = fundsConfirmation;
    }

    public boolean getFundsConfirmation() {
        return fundsConfirmation;
    }

    public void setFundsConfirmation(boolean fundsConfirmation) {
        this.fundsConfirmation = fundsConfirmation;
    }
}
