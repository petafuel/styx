package net.petafuel.styx.core.keepalive.workers;

import net.petafuel.styx.core.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFailureException;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Grabs any available in the queue
 */
public class CoreWorker extends RunnableWorker {
    private static final Logger LOG = LogManager.getLogger(CoreWorker.class);

    private boolean running;

    public CoreWorker() {
        this.setId(UUID.randomUUID());
        this.setType(WorkerType.CORE);
        this.running = true;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("KeepAlive-Worker-" + getType().toString() + "-" + getId().toString());
        while (running) {
            if (ThreadManager.getInstance().getCoreQueue().isEmpty()) {
                LOG.info("No polling from queue: Queue is empty -> ideling/waiting");
                try {
                    synchronized (ThreadManager.getInstance().getCoreQueue()) {
                        ThreadManager.getInstance().getCoreQueue().wait();
                    }
                } catch (InterruptedException e) {
                    //TODO Error handling
                    LOG.error("Interrupted");
                    Thread.currentThread().interrupt();
                }
            }
            WorkableTask task;
            synchronized (ThreadManager.getInstance().getCoreQueue()) {
                task = ThreadManager.getInstance().getCoreQueue().poll();
                LOG.info("Core Queue size: {}", ThreadManager.getInstance().getCoreQueue().size());
            }
            if (task == null) {
                continue;
            }
            LOG.info("Polled from queue: Task id:{} signature:{} is going to be executed", task.getId(), task.getSignature());
            try {
                TaskRecoveryDB.setRunning(task);
                task.execute();
                TaskRecoveryDB.setDone(task);
            } catch (TaskFailureException failure) {
                //TODO Error handling
                TaskRecoveryDB.changeWorker(task, WorkerType.RETRY_FAILURE);
            }
        }
    }
}
