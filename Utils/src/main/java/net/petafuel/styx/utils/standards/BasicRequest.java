package net.petafuel.styx.utils.standards;

public abstract class BasicRequest
{
    public abstract Object getHeaders();
    public abstract Object getBody();
    public abstract Object getMethod();
    public abstract Object getURL();

}
