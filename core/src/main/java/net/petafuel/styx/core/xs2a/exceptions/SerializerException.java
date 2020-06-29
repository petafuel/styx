package net.petafuel.styx.core.xs2a.exceptions;

public class SerializerException extends RuntimeException {
    public SerializerException(String msg)
    {
        super(msg);
    }
    public SerializerException(String msg, Exception e)
    {
        super(msg, e);
    }
}
