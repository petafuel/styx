package net.petafuel.styx.core.xs2a.entities;

public abstract class StrongAuthenticatableResource {
    protected SCA sca;

    public final SCA getSca() {
        return sca;
    }

    public final void setSca(SCA sca) {
        this.sca = sca;
    }
}
