package net.petafuel.styx.core.keepalive.entities;

public class TaskFailureException extends RuntimeException {
    public TaskFailureException(String message, Throwable e) {
        super(message, e);
    }
}
