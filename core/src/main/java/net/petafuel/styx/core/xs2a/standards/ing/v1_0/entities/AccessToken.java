package net.petafuel.styx.core.xs2a.standards.ing.v1_0.entities;

import javax.json.bind.annotation.JsonbProperty;

public class AccessToken {

    @JsonbProperty("access_token")
    private String token;
    @JsonbProperty("expires_in")
    private int expiresIn;
    @JsonbProperty("client_id")
    private String clientId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
