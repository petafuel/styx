package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class AvailableSCAApproaches {
    @JsonbProperty("redirect")
    private Boolean redirect;

    @JsonbProperty("oAuth")
    private Boolean oAuth;

    @JsonbProperty("decoupled")
    private Boolean decoupled;

    @JsonbProperty("embedded")
    private Boolean embedded;

    public Boolean getRedirect() {
        return redirect;
    }

    public void setRedirect(Boolean redirect) {
        this.redirect = redirect;
    }

    public Boolean getoAuth() {
        return oAuth;
    }

    public void setoAuth(Boolean oAuth) {
        this.oAuth = oAuth;
    }

    public Boolean getDecoupled() {
        return decoupled;
    }

    public void setDecoupled(Boolean decoupled) {
        this.decoupled = decoupled;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }
}
