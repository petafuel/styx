package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.entities.SCA;

import javax.json.bind.annotation.JsonbProperty;

public class GetSCAStatusResponse {
    private SCA.Status scaStatus;

    /**
     * @deprecated Default constructor for Jsonb
     */
    @Deprecated
    public GetSCAStatusResponse() {
        //Default constructor for Jsonb
    }

    public SCA.Status getScaStatus() {
        return scaStatus;
    }

    @JsonbProperty("scaStatus")
    public void setScaStatus(String scaStatus) {
        this.scaStatus = SCA.Status.valueOf(scaStatus.toUpperCase());
    }
}
