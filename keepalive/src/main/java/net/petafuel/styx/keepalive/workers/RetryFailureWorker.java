package net.petafuel.styx.keepalive.workers;

import net.petafuel.styx.core.xs2a.utils.Config;
import net.petafuel.styx.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.KeepAliveProperties;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureException;
import net.petafuel.styx.keepalive.entities.TaskRetryFailureException;
import net.petafuel.styx.keepalive.entities.TaskState;
import net.petafuel.styx.keepalive.entities.WorkerType;
import net.petafuel.styx.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Retry Failure Worker to process Task which failed on first try
 */
public final class RetryFailureWorker extends RunnableWorker {
    private static final Logger LOG = LogManager.getLogger(RetryFailureWorker.class);

    private final AtomicBoolean running;
    private final AtomicLong currentTaskStartTime;
    private final AtomicReference<WorkableTask> currentTask;
    private final int maxRetriesPerTask;

    public RetryFailureWorker() {
        this.setId(UUID.randomUUID());
        this.setType(WorkerType.RETRY_FAILURE);
        this.running = new AtomicBoolean(false);
        this.currentTaskStartTime = new AtomicLong();
        this.currentTask = new AtomicReference<>();
        this.maxRetriesPerTask = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREADS_RETRYFAILUREWORKER_MAX_EXEC_RETRIES.getPropertyPath(), "3"));
    }

    @Override
    public void run() {
        Thread.currentThread().setName("KeepAlive-Worker-" + getType().toString() + "-" + getId().toString());
        LOG.info("Started RetryFailureWorker id: {}", this.getId());
        this.setRunning(true);
        while (running.get()) {
            if (ThreadManager.getInstance().getRetryFailureQueue().isEmpty()) {
                LOG.info("No polling from queue: Queue is empty -> ideling/waiting");
                try {
                    synchronized (ThreadManager.getInstance().getRetryFailureQueue()) {
                        ThreadManager.getInstance().getRetryFailureQueue().wait();
                    }
                } catch (InterruptedException e) {
                    LOG.error("RetryFailure Worker {} was interrupted", this.getId());
                    Thread.currentThread().interrupt();
                }
            }
            WorkableTask task;
            synchronized (ThreadManager.getInstance().getRetryFailureQueue()) {
                task = ThreadManager.getInstance().getRetryFailureQueue().poll();
            }
            if (task == null) {
                continue;
            }
            currentTask.set(task);
            LOG.info("Task id:{} signature:{} polled from queue", task.getId(), task.getSignature());
            try {
                if (TaskRecoveryDB.incrementExecutionCounter(task.getId()) >= maxRetriesPerTask) {
                    throw new TaskFinalFailureException("Maximum amount of executions by a RetryFailureWorker was reached for task: " + task.getId(), TaskFinalFailureCode.EXCEEDED_MAX_RETRIES_THROUGH_RETRYFAILUREWORKER);
                }

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
            } catch (Throwable throwable) {
                LOG.error("Task id: {} signature: {} encountered an unexpected error: {} -> {}", task.getId(), task.getSignature(), throwable.getClass().getSimpleName(), throwable.getMessage());
                TaskRecoveryDB.setFinallyFailed(task.getId(), throwable.getMessage(), TaskFinalFailureCode.UNKNOWN);
            }
        }
        LOG.info("Terminated RetryFailureWorker id: {}", this.getId());
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
