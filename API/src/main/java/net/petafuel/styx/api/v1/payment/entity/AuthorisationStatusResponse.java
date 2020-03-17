package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.SCA;

import javax.json.bind.annotation.JsonbProperty;

public class AuthorisationStatusResponse {

    private SCA.Status scaStatus;

    public String getScaStatus() {
        return scaStatus.getValue();
    }

    @JsonbProperty("scaStatus")
    public void setScaStatus(String scaStatus) {
        this.scaStatus = SCA.Status.valueOf(scaStatus.toUpperCase());
    }
}
