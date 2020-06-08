package net.petafuel.styx.keepalive.threads;

import net.petafuel.styx.core.xs2a.utils.Config;
import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.TaskState;
import net.petafuel.styx.keepalive.entities.WorkerType;
import net.petafuel.styx.keepalive.recovery.RecoveryRoutine;
import net.petafuel.styx.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.keepalive.workers.CoreWorker;
import net.petafuel.styx.keepalive.workers.RetryFailureWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.MANAGER_PROBE_FREQUENCY;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.MANAGER_PROBE_INITIAL_DELAY;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.MANAGER_USE_RECOVERY;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.THREADS_COREWORKER_MAX_AMOUNT;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.THREADS_COREWORKER_MIN_AMOUNT;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.THREADS_COREWORKER_SPAWN_THRESHOLD;
import static net.petafuel.styx.keepalive.entities.KeepAliveProperties.THREADS_RETRYFAILUREWORKER_MAX_AMOUNT;

/**
 * Manages the task queues and worker threads
 */
public final class ThreadManager {
    private static final Logger LOG = LogManager.getLogger(ThreadManager.class);

    private final ConcurrentLinkedQueue<WorkableTask> retryFailureQueue;
    private final int minCoreWorkers;
    private final int maxCoreWorkers;
    private final int coreWorkerSpawnThreshold;
    private final int maxRetryFailureWorkers;
    private final boolean useRecovery;
    private final int probeFrequency;
    private final int probeInitialDelay;
    private final ConcurrentLinkedQueue<WorkableTask> coreQueue;
    private final List<RetryFailureWorker> retryFailureWorkers;
    private final ThreadPoolExecutor retryFailurePool;
    private final List<CoreWorker> coreWorkers;
    private final ThreadPoolExecutor corePool;
    private boolean initialized;

    private ThreadManager() {
        LOG.info("Initializing KeepAlive ThreadManager");
        this.initialized = false;
        this.coreWorkers = new ArrayList<>();
        this.coreQueue = new ConcurrentLinkedQueue<>();
        this.minCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(THREADS_COREWORKER_MIN_AMOUNT.getPropertyPath(), "4"));
        this.maxCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(THREADS_COREWORKER_MAX_AMOUNT.getPropertyPath(), "12"));
        this.coreWorkerSpawnThreshold = Integer.parseInt(Config.getInstance().getProperties().getProperty(THREADS_COREWORKER_SPAWN_THRESHOLD.getPropertyPath(), "10"));

        this.maxRetryFailureWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(THREADS_RETRYFAILUREWORKER_MAX_AMOUNT.getPropertyPath(), "20"));
        this.retryFailureWorkers = new ArrayList<>();
        this.retryFailureQueue = new ConcurrentLinkedQueue<>();

        this.probeFrequency = Integer.parseInt(Config.getInstance().getProperties().getProperty(MANAGER_PROBE_FREQUENCY.getPropertyPath(), "3000"));
        this.probeInitialDelay = Integer.parseInt(Config.getInstance().getProperties().getProperty(MANAGER_PROBE_INITIAL_DELAY.getPropertyPath(), "0"));
        this.useRecovery = Boolean.parseBoolean(Config.getInstance().getProperties().getProperty(MANAGER_USE_RECOVERY.getPropertyPath(), "true"));

        if (this.minCoreWorkers <= 0 || maxCoreWorkers < 1) {
            throw new IllegalArgumentException("ThreadManager cannot be initialized with zero workers, see config.properties");
        }

        this.corePool = new ThreadPoolExecutor(
                minCoreWorkers,
                maxCoreWorkers,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        LOG.info("KeepAlive Setup~ min/max CoreWorker Threads: {}/{}", minCoreWorkers, maxCoreWorkers);

        this.retryFailurePool = new ThreadPoolExecutor(
                1,
                maxRetryFailureWorkers,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        LOG.info("Starting KeepAlive ThreadManagement");
        spawnStartupThreads();
        initialized = true;
        if (useRecovery) {
            RecoveryRoutine.runTaskRecovery();
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::probeWorkers, probeInitialDelay, probeFrequency, TimeUnit.MILLISECONDS);
    }

    public void queueTask(WorkableTask task) {
        this.queueTask(task, WorkerType.CORE);
    }

    public void queueTask(WorkableTask task, WorkerType workerType) {
        if (!initialized) {
            LOG.error("Cannot queue Task, ThreadManager is not initialized yet ThreadManager.start() was not called");
            return;
        }
        switch (workerType) {
            case CORE:
                this.coreQueue.add(task);
                TaskRecoveryDB.createTask(task, workerType);
                TaskRecoveryDB.updateState(task.getId(), TaskState.QUEUED);
                synchronized (this.coreQueue) {
                    this.coreQueue.notifyAll();
                }
                break;
            case RETRY_FAILURE:
                this.retryFailureQueue.add(task);
                TaskRecoveryDB.updateState(task.getId(), TaskState.QUEUED);
                synchronized (this.retryFailureQueue) {
                    this.retryFailureQueue.notifyAll();
                }
                break;
            default:
                LOG.warn("Task id:{} name:{} was queued with an unknown priority, executing as CORE", task.getId(), task.getSignature());
                this.coreQueue.add(task);
                TaskRecoveryDB.updateState(task.getId(), TaskState.QUEUED);
                break;
        }
    }

    private void spawnStartupThreads() {
        IntStream.range(0, minCoreWorkers).forEach(i -> {
            CoreWorker coreWorker = new CoreWorker();
            LOG.info("KeepAlive Setup~ Spawning Worker type:{} id:{}", coreWorker.getType(), coreWorker.getId());
            ThreadSpawner.spawn(coreWorker);
        });

        ThreadSpawner.spawn(new RetryFailureWorker());
    }

    public ThreadPoolExecutor getCorePool() {
        return corePool;
    }

    private void probeWorkers() {
        checkCoreRuntime();
        checkRetryFailureRuntime();
    }

    List<CoreWorker> getCoreWorkers() {
        return coreWorkers;
    }

    public Queue<WorkableTask> getRetryFailureQueue() {
        return retryFailureQueue;
    }

    public ThreadPoolExecutor getRetryFailurePool() {
        return retryFailurePool;
    }

    public List<RetryFailureWorker> getRetryFailureWorkers() {
        return retryFailureWorkers;
    }

    public Queue<WorkableTask> getCoreQueue() {
        return this.coreQueue;
    }

    private void checkCoreRuntime() {
        float workerAmount = (float) this.corePool.getActiveCount();
        float taskAmount = (float) this.coreQueue.size();
        float spawnThreshold = (taskAmount / this.coreWorkerSpawnThreshold);

        LOG.debug("Probing CoreWorkers... queue size: {}, amount workers: {}, current threshold: {}", (int) taskAmount, (int) workerAmount, spawnThreshold);

        if (spawnThreshold > workerAmount) {
            if (workerAmount < this.maxCoreWorkers) {
                //spawn new workers on high load
                LOG.info("Spawning new CoreWorker coreWorkerSpawnThreshold: {} > currentWorkers: {} -> Current Queue size: {}", spawnThreshold, workerAmount, this.coreQueue.size());
                this.corePool.setCorePoolSize(this.corePool.getPoolSize() + 1);
                ThreadSpawner.spawn(new CoreWorker());
            } else {
                LOG.warn("Cannot spawn new CoreWorkers, limit reached. Amount CoreWorkers: {} Queue-size: {}", workerAmount, taskAmount);
            }
        } else if (workerAmount > this.minCoreWorkers && this.coreWorkers.size() > this.minCoreWorkers) {
            LOG.debug("Despawning CoreWorker, new pool size {}", (int) workerAmount - 1);
            this.corePool.setCorePoolSize(this.corePool.getPoolSize() - 1);
            Optional<CoreWorker> worker = this.coreWorkers.stream().findAny();
            worker.ifPresent(coreWorker -> {
                coreWorker.setRunning(false);
                this.coreWorkers.remove(coreWorker);
            });
        }
        if (workerAmount >= this.maxCoreWorkers) {
            LOG.warn("Reached hard limit of {} for parallel running CoreWorkers. Amount CoreWorkers: {} Queue-size: {}", maxCoreWorkers, workerAmount, taskAmount);
        }
    }

    private void checkRetryFailureRuntime() {
        float workerAmount = (float) this.retryFailurePool.getActiveCount();
        float taskAmount = (float) this.retryFailureQueue.size();

        LOG.debug("Probing RetryFailureWorkers... queue size: {}, amount workers: {}", (int) taskAmount, (int) workerAmount);

        if (taskAmount > 0 && workerAmount < this.maxRetryFailureWorkers) {
            this.retryFailurePool.setCorePoolSize(this.retryFailurePool.getPoolSize() + 1);
            ThreadSpawner.spawn(new RetryFailureWorker());
        } else if (taskAmount == 0) {
            Optional<RetryFailureWorker> worker = this.retryFailureWorkers.stream().findAny();
            worker.ifPresent(retryFailureWorker -> {
                retryFailureWorker.setRunning(false);
                this.retryFailureWorkers.remove(retryFailureWorker);
            });
        }

    }

    private static class Holder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
}

