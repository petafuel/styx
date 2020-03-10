package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.PSUData;

public class StartSCARequest {
    private PSUData psuData;
    private String authenticationMethodId;

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
    }

    public String getAuthenticationMethodId() {
        return authenticationMethodId;
    }

    public void setAuthenticationMethodId(String authenticationMethodId) {
        this.authenticationMethodId = authenticationMethodId;
    }
}
