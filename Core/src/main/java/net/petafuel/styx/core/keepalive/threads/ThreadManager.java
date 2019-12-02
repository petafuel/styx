package net.petafuel.styx.core.keepalive.threads;

import net.petafuel.styx.core.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.KeepAliveProperties;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.recovery.RecoveryRoutine;
import net.petafuel.styx.core.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.core.keepalive.workers.CoreWorker;
import net.petafuel.styx.core.keepalive.workers.RetryFailureWorker;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Manages the task queues and worker threads
 */
public final class ThreadManager {
    private static final Logger LOG = LogManager.getLogger(ThreadManager.class);


    private final ConcurrentLinkedQueue<WorkableTask> retryFailureQueue;
    private final int minCoreWorkers;
    private final int maxCoreWorkers;
    private final int coreWorkerSpawnThreshold;
    private final int coreWorkerFrozenThreshold;
    private final boolean useRecovery;
    private final int probeFrequency;
    private final int probeInitialDelay;
    private final ConcurrentLinkedQueue<WorkableTask> coreQueue;
    private ThreadPoolExecutor retryFailurePool;
    private List<RetryFailureWorker> retryFailureWorkers;
    private ThreadPoolExecutor corePool;
    private List<CoreWorker> coreWorkers;

    private ThreadManager() {
        LOG.info("Starting KeepAlive ThreadManager");

        //loading all necessary config flags into memory
        this.minCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_COREQUEUE_MIN_WORKERS.getPropertyPath(), "4"));
        this.maxCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_COREQUEUE_MAX_WORKERS.getPropertyPath(), "12"));
        this.coreWorkerSpawnThreshold = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_COREQUEUE_SPAWN_THRESHOLD.getPropertyPath(), "10"));
        this.coreWorkerFrozenThreshold = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_COREQUEUE_WORKER_FROZEN_THRESHOLD.getPropertyPath(), "10"));

        this.coreWorkers = new ArrayList<>();
        this.coreQueue = new ConcurrentLinkedQueue<>();

        this.retryFailureWorkers = new ArrayList<>();
        this.retryFailureQueue = new ConcurrentLinkedQueue<>();

        this.probeFrequency = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_PROBE_FREQUENCY.getPropertyPath(), "3000"));
        this.probeInitialDelay = Integer.parseInt(Config.getInstance().getProperties().getProperty(KeepAliveProperties.THREAD_PROBE_INITIAL_DELAY.getPropertyPath(), "0"));
        this.useRecovery = Boolean.parseBoolean(Config.getInstance().getProperties().getProperty(KeepAliveProperties.USE_RECOVERY.getPropertyPath(), "true"));


        if (this.minCoreWorkers <= 0 || maxCoreWorkers < 1) {
            throw new IllegalArgumentException("Threadmmanager cannot be initialized with zero workers, see config.properties");
        }

        this.corePool = new ThreadPoolExecutor(
                minCoreWorkers,
                maxCoreWorkers,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        LOG.info("KeepAlive Setup~ min/max CoreWorker Threads: {}/{}", minCoreWorkers, maxCoreWorkers);

        this.retryFailurePool = new ThreadPoolExecutor(
                4,
                10,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        spawnStartupThreads();
        if (useRecovery) {
            RecoveryRoutine.runTaskRecovery();
        }
        //TODO add thread that checks for Worker Timeouts by task signature(Too long for one task) and also checks the workload to spawn or despawn Workers

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::probeWorkers, probeInitialDelay, probeFrequency, TimeUnit.MILLISECONDS);
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
            case RETRY_FAILURE:
                this.retryFailureQueue.add(task);
                TaskRecoveryDB.setQueued(task, workerType);
                synchronized (this.retryFailureQueue) {
                    this.retryFailureQueue.notifyAll();
                }
                break;
            case INSTANT_SPAWN:
                //TODO remove ?!
                break;
            default:
                LOG.warn("Task id:{} name:{} was queued with an unknown priority, executing as CORE", task.getId(), task.getSignature());
                this.coreQueue.add(task);
                break;
        }
    }

    public Queue<WorkableTask> getCoreQueue() {
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

    private void probeWorkers() {
        float workerAmount = (float) this.corePool.getActiveCount();
        float taskAmount = (float) this.coreQueue.size();
        float threshold = (taskAmount / this.coreWorkerSpawnThreshold);

        LOG.debug("Probing Workers... queue size: {}, amount workers: {}, current threshold: {}", (int) taskAmount, (int) workerAmount, threshold);

        if (threshold > workerAmount) {
            if (workerAmount < this.maxCoreWorkers) {
                //spawn new workers on high load
                LOG.info("Spawning new CoreWorker coreWorkerSpawnThreshold: {} > currentWorkers: {} -> Current Queue size: {}", threshold, workerAmount, this.coreQueue.size());
                this.corePool.setCorePoolSize(this.corePool.getPoolSize() + 1);
                ThreadSpawner.spawn(new CoreWorker());
            } else {
                LOG.warn("Cannot spawn new Workers, limit reached. Amount CoreWorkers: {} Queue-size: {}", workerAmount, taskAmount);
            }
        } else if (workerAmount > this.minCoreWorkers && this.coreWorkers.size() > this.minCoreWorkers) {
            LOG.debug("Despawning worker, new pool size {}", (int) workerAmount - 1);
            this.corePool.setCorePoolSize(this.corePool.getPoolSize() - 1);
            RunnableWorker worker = this.coreWorkers.get(0);
            worker.setRunning(false);
            this.coreWorkers.remove(worker);
/*            synchronized (coreQueue) {
                coreQueue.notifyAll();
            }*/
        }
        if (workerAmount >= this.maxCoreWorkers) {
            LOG.warn("Reached hard limit of {} for core Workers. Amount CoreWorkers: {} Queue-size: {}", maxCoreWorkers, workerAmount, taskAmount);
        }
        List<CoreWorker> frozenWorkers = this.coreWorkers.parallelStream().filter(worker -> worker.getCurrentTaskStartTime().get() >= this.coreWorkerFrozenThreshold).collect(Collectors.toList());
        //frozenWorkers.parallelStream().forEach(frozenWorker -> frozenWorker.get);
    }

    List<CoreWorker> getCoreWorkers() {
        return coreWorkers;
    }

    public ConcurrentLinkedQueue<WorkableTask> getRetryFailureQueue() {
        return retryFailureQueue;
    }

    public ThreadPoolExecutor getRetryFailurePool() {
        return retryFailurePool;
    }

    public List<RetryFailureWorker> getRetryFailureWorkers() {
        return retryFailureWorkers;
    }

    private static class Holder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
}

