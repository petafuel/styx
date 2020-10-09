package net.petafuel.styx.api.v1.status.entity;

/**
 * Model to provide informations about an internal redirect onto styx's status pages
 */
public class RedirectStatus {
    private final StatusType statusType;
    private final String identification;
    private final RedirectStep redirectStep;

    private static final String NOT_PROVIDED = "not-provided";

    public RedirectStatus(StatusType statusType, String identification) {
        this(statusType, identification, null);
    }

    public RedirectStatus(StatusType statusType, String identification, RedirectStep redirectStep) {
        this.statusType = statusType;
        if(identification == null){
            identification = NOT_PROVIDED;
        }
        this.identification = identification;
        this.redirectStep = redirectStep;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public String getIdentification() {
        return identification;
    }

    public RedirectStep getRedirectStep() {
        return redirectStep;
    }
}
