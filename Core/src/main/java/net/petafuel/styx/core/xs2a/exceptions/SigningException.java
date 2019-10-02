package net.petafuel.styx.core.xs2a.exceptions;

public class SigningException extends RuntimeException {
    public SigningException(String msg, Exception e) {
        super(msg, e);
    }

}
