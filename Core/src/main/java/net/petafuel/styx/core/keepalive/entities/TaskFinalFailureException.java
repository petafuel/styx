package net.petafuel.styx.core.keepalive.entities;

/**
 * Defines an exception which should be thrown in case a Task cannot be executed anymore, even by retrying
 */
public final class TaskFinalFailureException extends RuntimeException {
    final TaskFinalFailureCode code;

    public TaskFinalFailureException(String message, TaskFinalFailureCode code) {
        super(message);
        this.code = code;
    }

    public TaskFinalFailureException(String message) {
        super(message);
        this.code = TaskFinalFailureCode.UNKNOWN;
    }

    public TaskFinalFailureCode getCode() {
        return this.code;
    }
}
