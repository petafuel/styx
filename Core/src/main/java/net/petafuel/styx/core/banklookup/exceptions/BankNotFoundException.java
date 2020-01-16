package net.petafuel.styx.core.banklookup.exceptions;

/**
 * use if there was no entry in SAD for a specified bic
 */
public class BankNotFoundException extends Exception {
    public BankNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BankNotFoundException(String message) {
        super(message);
    }
}
