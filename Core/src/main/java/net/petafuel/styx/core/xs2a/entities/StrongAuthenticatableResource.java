package net.petafuel.styx.core.xs2a.entities;

abstract public class StrongAuthenticatableResource {
    protected SCA sca;

    public final SCA getSca() {
        return sca;
    }

    public final void setSca(SCA sca) {
        this.sca = sca;
    }
}
