package net.petafuel.styx.core.keepalive.entities;

public enum KeepAliveProperties {
    THREAD_COREQUEUE_MIN_WORKERS("keepalive.threads.coreQueue.minWorkers"),
    THREAD_COREQUEUE_MAX_WORKERS("keepalive.threads.coreQueue.maxWorkers"),
    THREAD_COREQUEUE_SPAWN_THRESHOLD("keepalive.threads.coreQueue.spawnThresholdTasksPerWorker"),
    THREAD_COREQUEUE_WORKER_FROZEN_THRESHOLD("keepalive.threads.coreQueue.workerFrozenThresholdMS"),
    USE_RECOVERY("keepalive.useRecovery"),
    THREAD_PROBE_FREQUENCY("keepalive.threads.probeFrequencyMS"),
    THREAD_PROBE_INITIAL_DELAY("keepalive.threads.probeInitialDelay");

    private String propertyPath;

    KeepAliveProperties(String path) {
        this.propertyPath = path;
    }

    public String getPropertyPath() {
        return this.propertyPath;
    }
}
