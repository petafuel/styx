package net.petafuel.styx.core.keepalive.entities;

/**
 * Failure Codes for Tasks that cannot be executed at all
 */
public enum TaskFinalFailureCode {
    UNKNOWN(0),
    UNRECOVERABLE_STATUS(1),
    RECOVERED_AND_QUEUED(2),
    POLL_ON_NOT_EXISTING_CONSENT(3);

    private final int value;

    TaskFinalFailureCode(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
