package net.petafuel.styx.core.banklookup.exceptions;

/**
 * Use if there was an error while SAD trying to initialize related service classes
 */
public class BankLookupFailedException extends Exception {
    public BankLookupFailedException(String message) {
        super(message);
    }

    public BankLookupFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
