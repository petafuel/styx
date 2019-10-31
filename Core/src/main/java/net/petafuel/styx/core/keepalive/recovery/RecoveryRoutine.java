package net.petafuel.styx.core.keepalive.recovery;

import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

/**
 * Runs the Recovery routine on application startup
 * Checks previously queued and interrupted task from a previous execution
 */
public class RecoveryRoutine {
    private final static Logger LOG = LogManager.getLogger(RecoveryRoutine.class);

    private RecoveryRoutine() {
    }

    public static void runTaskRecovery() {
        LOG.info("Running Task Recovery Routine");
        LinkedHashMap<WorkerType, WorkableTask> interruptedTasks = TaskRecoveryDB.getInterruptedTasks();
        LinkedHashMap<WorkerType, WorkableTask> queuedTasks = TaskRecoveryDB.getQueuedTasks();
        LOG.info("Found {} queued tasks and {} interrupted tasks that need to be recovered", queuedTasks.size(), interruptedTasks.size());
        if (queuedTasks.isEmpty() && interruptedTasks.isEmpty()) {
            LOG.info("No tasks need to be recovered, Recovery is done");
            return;
        }
        LOG.info("Queueing recovered interrupted tasks amount: {}", interruptedTasks.size());
        interruptedTasks.forEach((workerType, task) -> {
            ThreadManager.getInstance().queueTask(task, workerType);
        });
        LOG.info("Queueing recovered queued tasks amount: {}", queuedTasks.size());
        queuedTasks.forEach((workerType, task) -> ThreadManager.getInstance().queueTask(task, workerType));
    }
}
