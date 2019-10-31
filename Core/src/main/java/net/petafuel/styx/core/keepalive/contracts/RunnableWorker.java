package net.petafuel.styx.core.keepalive.contracts;

import net.petafuel.styx.core.keepalive.entities.WorkerType;

import java.util.UUID;

public abstract class RunnableWorker implements Runnable {
    protected UUID id;
    protected WorkerType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkerType getType() {
        return type;
    }

    public void setType(WorkerType type) {
        this.type = type;
    }

    public abstract void setRunning(boolean running);
}
