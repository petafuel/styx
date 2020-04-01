package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbTransient;

public abstract class StrongAuthenticatableResource {

    @JsonbTransient
    protected SCA sca;

    public final SCA getSca() {
        return sca;
    }

    public final void setSca(SCA sca) {
        this.sca = sca;
    }
}
