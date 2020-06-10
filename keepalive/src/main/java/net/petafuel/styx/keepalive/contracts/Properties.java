package net.petafuel.styx.keepalive.contracts;

public final class Properties {
    public static final String THREADS_COREWORKER_MIN_AMOUNT = "keepalive.threads.coreWorker.minAmount";
    public static final String THREADS_COREWORKER_MAX_AMOUNT = "keepalive.threads.coreWorker.maxAmount";
    public static final String THREADS_COREWORKER_SPAWN_THRESHOLD = "keepalive.threads.coreWorker.spawnThresholdTasksPerWorker";

    public static final String THREADS_RETRYFAILUREWORKER_MAX_EXEC_RETRIES = "keepalive.threads.retryFailureWorker.maxExecutionRetries";
    public static final String THREADS_RETRYFAILUREWORKER_MAX_AMOUNT = "keepalive.threads.retryFailureWorker.maxAmount";

    public static final String MANAGER_USE_RECOVERY = "keepalive.manager.useRecovery";
    public static final String MANAGER_PROBE_FREQUENCY = "keepalive.manager.probeFrequencyMS";
    public static final String MANAGER_PROBE_INITIAL_DELAY = "keepalive.manager.probeInitialDelay";

    public static final String TASKS_CONSENTPOLL_AMOUNT_RETRIES = "keepalive.tasks.consentpoll.amountRetries";
    public static final String TASKS_CONSENTPOLL_TIMEOUT_BETWEEN_RETRIES = "keepalive.tasks.consentpoll.timoutBetweenRetriesMS";

    public static final String PAYMENT_STATUS_POLL_MAX_EXECUTION_TIME = "keepalive.tasks.paymentstatuspoll.maxExecutionTimeMS";
    public static final String PAYMENT_STATUS_HOOK_SERVICE = "keepalive.tasks.paymentstatuspoll.service";
    public static final String PAYMENT_STATUS_MAX_REQUEST_FAILURES = "keepalive.tasks.paymentstatuspoll.maxRequestFailures";
    public static final String PAYMENT_STATUS_TIMEOUT_BETWEEN_RETRIES = "keepalive.tasks.paymentstatuspoll.timoutBetweenRetriesMS";

    private Properties() {
    }
}
