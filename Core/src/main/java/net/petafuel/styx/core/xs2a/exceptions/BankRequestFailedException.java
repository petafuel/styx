package net.petafuel.styx.core.xs2a.exceptions;

public class BankRequestFailedException extends Exception {
    private Integer httpStatusCode;

    public BankRequestFailedException(String message, Exception e) {
        super(message, e);
    }

    public BankRequestFailedException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public BankRequestFailedException(String message, Exception e, int httpStatusCode) {
        super(message, e);
        this.httpStatusCode = httpStatusCode;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}
