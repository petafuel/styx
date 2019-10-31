package net.petafuel.styx.core.keepalive.threads;

import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.recovery.RecoveryRoutine;
import net.petafuel.styx.core.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.core.keepalive.workers.CoreWorker;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Manages the task queues and worker threads
 */
public class ThreadManager {
    private static final String PROPERTY_THREAD_COREQUEUE_MIN_WORKERS = "keepalive.threads.coreQueue.minWorkers";
    private static final String PROPERTY_THREAD_COREQUEUE_MAX_WORKERS = "keepalive.threads.coreQueue.maxWorkers";

    private static final Logger LOG = LogManager.getLogger(ThreadManager.class);

    /**
     * Failed tasks / too many retries irgend was machen damit
     */
    private final ConcurrentLinkedQueue<WorkableTask> coreQueue;
    private ConcurrentLinkedQueue<WorkableTask> failureQueue;
    private ThreadPoolExecutor corePool;

    private int minCoreWorkers;
    private int maxCoreWorkers;

    private ThreadManager() {
        LOG.info("Starting KeepAlive ThreadManager");
        this.coreQueue = new ConcurrentLinkedQueue<>();
        this.failureQueue = new ConcurrentLinkedQueue<>();

        this.minCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(PROPERTY_THREAD_COREQUEUE_MIN_WORKERS, "4"));
        this.maxCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(PROPERTY_THREAD_COREQUEUE_MAX_WORKERS, "12"));

        this.corePool = new ThreadPoolExecutor(
                minCoreWorkers,
                maxCoreWorkers,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        LOG.info("KeepAlive Setup~ min/max CoreWorker Threads: {}/{}", minCoreWorkers, maxCoreWorkers);
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        spawnStartupThreads();
        RecoveryRoutine.runTaskRecovery();
        //TODO add thread that checks for Worker Timeouts by task signature(Too long for one task) and also checks the workload to spawn or despawn Workers
        //TODO Configurate threshold for new worker spawnings in config.properties
    }

    public void queueTask(WorkableTask task) {
        this.queueTask(task, WorkerType.CORE);
    }

    public void queueTask(WorkableTask task, WorkerType workerType) {
        switch (workerType) {
            case CORE:
                this.coreQueue.add(task);
                TaskRecoveryDB.setQueued(task, workerType);
                synchronized (this.coreQueue) {
                    this.coreQueue.notifyAll();
                }
                LOG.info("Core Queue size: {}", this.coreQueue.size());
                break;
            case DEDICATED:
                break;
            case RETRY_FAILURE:
                this.failureQueue.add(task);
                break;
            case INSTANT_SPAWN:
                break;
            default:
                LOG.warn("Task id:{} name:{} was queued with an unknown priority, executing as BASIC", task.getId(), task.getSignature());
                this.coreQueue.add(task);
                break;
        }
    }

    public ConcurrentLinkedQueue<WorkableTask> getCoreQueue() {
        return coreQueue;
    }

    private void spawnStartupThreads() {
        IntStream.range(0, minCoreWorkers).forEach(i -> {
            CoreWorker coreWorker = new CoreWorker();
            LOG.info("KeepAlive Setup~ Spawning Worker type:{} id:{}", coreWorker.getType(), coreWorker.getId());
            ThreadSpawner.spawn(coreWorker);
        });

    }

    public ThreadPoolExecutor getCorePool() {
        return corePool;
    }

    private static class Holder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
}

