package net.petafuel.styx.core.keepalive.workers;

import net.petafuel.styx.core.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureException;
import net.petafuel.styx.core.keepalive.entities.TaskRetryFailureException;
import net.petafuel.styx.core.keepalive.entities.TaskState;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Basic Worker that will execute Tasks from the coreQueue
 */
public final class CoreWorker extends RunnableWorker {
    private static final Logger LOG = LogManager.getLogger(CoreWorker.class);

    private final AtomicBoolean running;
    private final AtomicLong currentTaskStartTime;
    private final AtomicReference<WorkableTask> currentTask;

    public CoreWorker() {
        this.setId(UUID.randomUUID());
        this.setType(WorkerType.CORE);
        this.running = new AtomicBoolean(false);
        this.currentTaskStartTime = new AtomicLong(0);
        this.currentTask = new AtomicReference<>();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("KeepAlive-Worker-" + getType().toString() + "-" + getId().toString());
        LOG.info("Started CoreWorker id: {}", this.getId());
        setRunning(true);
        while (running.get()) {
            if (ThreadManager.getInstance().getCoreQueue().isEmpty()) {
                LOG.info("No polling from queue: Queue is empty -> ideling/waiting");
                try {
                    synchronized (ThreadManager.getInstance().getCoreQueue()) {
                        ThreadManager.getInstance().getCoreQueue().wait();
                    }
                } catch (InterruptedException e) {
                    LOG.error("CoreWorker {} was interrupted", this.getId());
                    Thread.currentThread().interrupt();
                }
            }
            WorkableTask task;
            synchronized (ThreadManager.getInstance().getCoreQueue()) {
                task = ThreadManager.getInstance().getCoreQueue().poll();
            }
            if (task == null) {
                continue;
            }
            currentTask.set(task);
            LOG.info("Task id:{} signature:{} polled from queue", task.getId(), task.getSignature());
            try {
                TaskRecoveryDB.updateState(task.getId(), TaskState.RUNNING);
                currentTaskStartTime.set(new Date().getTime());
                task.execute();
                LOG.info("Task id:{} signature: {} finished successfully", task.getId(), task.getSignature());
                currentTaskStartTime.set(0);
                TaskRecoveryDB.updateState(task.getId(), TaskState.DONE);
            } catch (TaskRetryFailureException retryFailure) {
                LOG.warn("Task id: {} signature: {} failed but will be requeued as RETRY_FAILURE -> {}", task.getId(), task.getSignature(), retryFailure.getMessage());
                TaskRecoveryDB.updateWorker(task.getId(), WorkerType.RETRY_FAILURE);
                ThreadManager.getInstance().queueTask(task, WorkerType.RETRY_FAILURE);
            } catch (TaskFinalFailureException finalFailure) {
                LOG.error("Task id: {} signature: {} finally failed with code:{} -> {}", task.getId(), task.getSignature(), finalFailure.getCode(), finalFailure.getMessage());
                TaskRecoveryDB.setFinallyFailed(task.getId(), finalFailure.getMessage(), finalFailure.getCode());
            }
        }
        LOG.info("Terminated CoreWorker id: {}", this.getId());
    }

    @Override
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public AtomicLong getCurrentTaskStartTime() {
        return currentTaskStartTime;
    }

    public AtomicReference<WorkableTask> getCurrentTask() {
        return currentTask;
    }
}
