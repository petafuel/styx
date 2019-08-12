package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;
import java.util.UUID;

public class Consent {

    public UUID getId() {
        return id;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public enum State {
        RECEIVED
    }

    public Consent(UUID id) {
        this.id = id;
    }

    // Identifier for Persistence
    private UUID id;

    // Identifier received from Bank
    private String consentId;

    private ConsentRequest request;

    private State state;

    private boolean recurringIndicator;

    private Date validUntil;

    private Date lastActionDate;

    private int frequencyPerDay;

    private SCAMethod chosenSCAMethod;

    private SCAMethod aspspSCAApproach;

}
