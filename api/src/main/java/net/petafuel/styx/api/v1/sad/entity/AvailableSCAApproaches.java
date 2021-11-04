package net.petafuel.styx.api.v1.sad.entity;

import javax.json.bind.annotation.JsonbProperty;

public class AvailableSCAApproaches {
    @JsonbProperty("redirect")
    private boolean redirect;

    @JsonbProperty("oAuth")
    private boolean oAuth;

    @JsonbProperty("decoupled")
    private boolean decoupled;

    @JsonbProperty("embedded")
    private boolean embedded;

    public boolean getRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public boolean getoAuth() {
        return oAuth;
    }

    public void setoAuth(boolean oAuth) {
        this.oAuth = oAuth;
    }

    public boolean getDecoupled() {
        return decoupled;
    }

    public void setDecoupled(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }
}
