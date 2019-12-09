package net.petafuel.styx.core.keepalive.entities;

public enum KeepAliveProperties {
    THREADS_COREWORKER_MIN_AMOUNT("keepalive.threads.coreWorker.minAmount"),
    THREADS_COREWORKER_MAX_AMOUNT("keepalive.threads.coreWorker.maxAmount"),
    THREADS_COREWORKER_SPAWN_THRESHOLD("keepalive.threads.coreWorker.spawnThresholdTasksPerWorker"),

    THREADS_RETRYFAILUREWORKER_MAX_EXEC_RETRIES("keepalive.threads.retryFailureWorker.maxExecutionRetries"),
    THREADS_RETRYFAILUREWORKER_MAX_AMOUNT("keepalive.threads.retryFailureWorker.maxAmount"),

    MANAGER_USE_RECOVERY("keepalive.manager.useRecovery"),
    MANAGER_PROBE_FREQUENCY("keepalive.manager.probeFrequencyMS"),
    MANAGER_PROBE_INITIAL_DELAY("keepalive.manager.probeInitialDelay"),

    TASKS_CONSENTPOLL_AMOUNT_RETRIES("keepalive.tasks.consentpoll.amountRetries"),
    TASKS_CONSENTPOLL_TIMEOUT_BETWEEN_RETRIES("keepalive.tasks.consentpoll.timoutBetweenRetriesMS");

    private final String propertyPath;

    KeepAliveProperties(String path) {
        this.propertyPath = path;
    }

    public String getPropertyPath() {
        return this.propertyPath;
    }
}
