package net.petafuel.styx.core.persistence;

public class PersistenceEmptyResultSetException extends PersistenceException {
    public PersistenceEmptyResultSetException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceEmptyResultSetException(String message) {
        super(message);
    }
}
