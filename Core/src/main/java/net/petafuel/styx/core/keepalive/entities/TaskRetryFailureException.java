package net.petafuel.styx.core.keepalive.entities;

/**
 * Defines a Exception in case of a Task failure. This exception should indicate that the failure is not final,
 * precisely it'S possible that the task is able to be solved successfully by retrying execution
 */
public class TaskRetryFailureException extends RuntimeException {
    public TaskRetryFailureException(String message, Throwable e) {
        super(message, e);
    }
}
