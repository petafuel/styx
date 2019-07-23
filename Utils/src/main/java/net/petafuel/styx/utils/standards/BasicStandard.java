package net.petafuel.styx.utils.standards;

import net.petafuel.styx.utils.Signer;

public abstract class BasicStandard implements XS2AStandardInterface
{
    protected Signer signer;
    protected AISInterface aisInterface;

    BasicStandard(Signer signer)
    {
        this.signer = signer;
    }

    public net.petafuel.styx.utils.standards.AISInterface getAis()
    {
        return aisInterface;
    }

    public boolean isAISImplemented()
    {
        return aisInterface != null;
    }
}
