package net.petafuel.styx.core.keepalive.contracts;

import java.util.UUID;

/**
 * define an executable task for a Worker
 */
public abstract class WorkableTask {

    private UUID id;

    protected WorkableTask() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public abstract String getSignature();

    public abstract void execute();
}
