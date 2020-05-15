package net.petafuel.styx.core.xs2a.exceptions;

public class OAuthTokenExpiredException extends Exception {

    public static final String MESSAGE = "The given preAuthId corresponds to an expired pre-step authorisation. A new pre-step is required.";

    public OAuthTokenExpiredException(String msg, Exception e) {
        super(msg, e);
    }

    public OAuthTokenExpiredException(String msg) {
        super(msg);
    }
}
