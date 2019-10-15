package net.petafuel.styx.core.xs2a.exceptions;

public class CertificateException extends RuntimeException {
    public CertificateException(String msg) {
        super(msg);
    }

    public CertificateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}