package net.petafuel.styx.keepalive.contracts;

public final class Properties {
    public static final String PAYMENT_STATUS_POLL_MAX_EXECUTION_TIME = "keepalive.tasks.paymentstatuspoll.maxExecutionTimeMS";
    public static final String PAYMENT_STATUS_HOOK_SERVICE = "keepalive.tasks.paymentstatuspoll.service";
    public static final String PAYMENT_STATUS_MAX_REQUEST_FAILURES = "keepalive.tasks.paymentstatuspoll.maxRequestFailures";
    public static final String PAYMENT_STATUS_TIMEOUT_BETWEEN_RETRIES = "keepalive.tasks.paymentstatuspoll.timoutBetweenRetriesMS";

    private Properties() {
    }
}
