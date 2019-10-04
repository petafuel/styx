package net.petafuel.styx.core.xs2a.exceptions;

public class BankRequestFailedException extends Exception {
    private int httpStatusCode;

    public BankRequestFailedException(String message, Exception e) {
        super(message, e);
    }

    public BankRequestFailedException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
