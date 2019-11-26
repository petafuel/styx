package net.petafuel.styx.core.keepalive.threads;

import net.petafuel.styx.core.keepalive.contracts.RunnableWorker;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.keepalive.recovery.RecoveryRoutine;
import net.petafuel.styx.core.keepalive.recovery.TaskRecoveryDB;
import net.petafuel.styx.core.keepalive.workers.CoreWorker;
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
import java.util.stream.IntStream;

/**
 * Manages the task queues and worker threads
 */
public final class ThreadManager {
    private static final String PROPERTY_THREAD_COREQUEUE_MIN_WORKERS = "keepalive.threads.coreQueue.minWorkers";
    private static final String PROPERTY_THREAD_COREQUEUE_MAX_WORKERS = "keepalive.threads.coreQueue.maxWorkers";
    private static final String PROPERTY_THREAD_COREQUEUE_SPAWN_THRESHOLD = "keepalive.threads.coreQueue.spawnThresholdTasksPerWorker";

    private static final Logger LOG = LogManager.getLogger(ThreadManager.class);

    /**
     * Failed tasks / too many retries irgend was machen damit
     */
    private final ConcurrentLinkedQueue<WorkableTask> coreQueue;
    private ConcurrentLinkedQueue<WorkableTask> failureQueue;
    private ThreadPoolExecutor corePool;
    private List<RunnableWorker> workers;

    private int minCoreWorkers;
    private int maxCoreWorkers;
    private int coreWorkerSpawnThreshold;

    private ThreadManager() {
        LOG.info("Starting KeepAlive ThreadManager");
        this.coreQueue = new ConcurrentLinkedQueue<>();
        this.failureQueue = new ConcurrentLinkedQueue<>();
        this.workers = new ArrayList<>();

        this.minCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(PROPERTY_THREAD_COREQUEUE_MIN_WORKERS, "4"));
        this.maxCoreWorkers = Integer.parseInt(Config.getInstance().getProperties().getProperty(PROPERTY_THREAD_COREQUEUE_MAX_WORKERS, "12"));
        this.coreWorkerSpawnThreshold = Integer.parseInt(Config.getInstance().getProperties().getProperty(PROPERTY_THREAD_COREQUEUE_SPAWN_THRESHOLD, "10"));

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
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        spawnStartupThreads();
        RecoveryRoutine.runTaskRecovery();
        //TODO add thread that checks for Worker Timeouts by task signature(Too long for one task) and also checks the workload to spawn or despawn Workers
        //TODO Configurate threshold for new worker spawnings in config.properties

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::probeWorkers, 0, 3, TimeUnit.SECONDS);
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
                //TODO remove ?!
                break;
            case RETRY_FAILURE:
                this.failureQueue.add(task);
                break;
            case INSTANT_SPAWN:
                //TODO remove ?!
                break;
            default:
                LOG.warn("Task id:{} name:{} was queued with an unknown priority, executing as BASIC", task.getId(), task.getSignature());
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
        } else if (workerAmount > this.minCoreWorkers && this.workers.size() > this.minCoreWorkers) {
            LOG.info("Despawning worker, new pool size {}", (int) workerAmount - 1);
            this.corePool.setCorePoolSize(this.corePool.getPoolSize() - 1);
            RunnableWorker worker = this.workers.get(0);
            worker.setRunning(false);
            this.workers.remove(worker);
/*            synchronized (coreQueue) {
                coreQueue.notifyAll();
            }*/
        }
        if (workerAmount >= this.maxCoreWorkers) {
            LOG.warn("Reached hard limit for core Workers. Amount CoreWorkers: {} Queue-size: {}", workerAmount, taskAmount);
        }
    }

    List<RunnableWorker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<RunnableWorker> workers) {
        this.workers = workers;
    }

    private static class Holder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
}

