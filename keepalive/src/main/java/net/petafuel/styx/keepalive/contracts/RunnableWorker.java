package net.petafuel.styx.keepalive.contracts;

import net.petafuel.styx.keepalive.entities.WorkerType;

import java.util.UUID;

public abstract class RunnableWorker implements Runnable {
    protected UUID id;
    protected WorkerType type;

    public final UUID getId() {
        return id;
    }

    public final void setId(UUID id) {
        this.id = id;
    }

    public final WorkerType getType() {
        return type;
    }

    public final void setType(WorkerType type) {
        this.type = type;
    }

    public abstract void setRunning(boolean running);
}
