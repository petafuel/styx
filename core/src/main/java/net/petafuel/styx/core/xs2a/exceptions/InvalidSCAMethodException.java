package net.petafuel.styx.core.xs2a.exceptions;

public class InvalidSCAMethodException extends RuntimeException {
    public InvalidSCAMethodException(String msg, Exception e) {
        super(msg, e);
    }

    public InvalidSCAMethodException(String msg) {
        super(msg);
    }

}
