package net.petafuel.styx.core.keepalive.threads;

import net.petafuel.styx.core.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.core.keepalive.entities.WorkerType;

import java.util.UUID;

public class ThreadSpawner {
    public static void spawn(RunnableWorker worker) {
        if (worker.getId() == null) {
            worker.setId(UUID.randomUUID());
        }
        if (worker.getType() == null) {
            worker.setType(WorkerType.CORE);
        }
        switch (worker.getType()) {
            case CORE:
                ThreadManager.getInstance().getCorePool().execute(worker);
                break;
            case DEDICATED:
                break;
            case INSTANT_SPAWN:
                break;
            default:
                break;
        }

    }
}
