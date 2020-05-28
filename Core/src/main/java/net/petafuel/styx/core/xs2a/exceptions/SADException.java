package net.petafuel.styx.core.xs2a.exceptions;

public class SADException extends RuntimeException {
    public SADException(String message) {
        super(message);
    }

    public SADException(String message, Throwable cause) {
        super(message, cause);
    }
}
