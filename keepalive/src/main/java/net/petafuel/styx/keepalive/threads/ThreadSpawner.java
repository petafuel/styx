package net.petafuel.styx.keepalive.threads;

import net.petafuel.styx.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.keepalive.entities.WorkerType;
import net.petafuel.styx.keepalive.workers.CoreWorker;
import net.petafuel.styx.keepalive.workers.RetryFailureWorker;

import java.util.UUID;

public final class ThreadSpawner {

    private ThreadSpawner(){}

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
                ThreadManager.getInstance().getCoreWorkers().add((CoreWorker) worker);
                break;
            case RETRY_FAILURE:
                ThreadManager.getInstance().getRetryFailurePool().execute(worker);
                ThreadManager.getInstance().getRetryFailureWorkers().add((RetryFailureWorker) worker);
                break;
            default:
                break;
        }

    }
}
