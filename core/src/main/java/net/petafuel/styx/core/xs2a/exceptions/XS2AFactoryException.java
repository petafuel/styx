package net.petafuel.styx.core.xs2a.exceptions;

public class XS2AFactoryException extends RuntimeException {
    public XS2AFactoryException() {
        super();
    }

    public XS2AFactoryException(String message) {
        super(message);
    }

    public XS2AFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
