package net.petafuel.styx.utils.banklookup;

import net.petafuel.styx.utils.standards.XS2AStandardInterface;

import java.net.URL;

public class BankInterfaceDescription
{
    private URL url;
    private XS2AStandardInterface standard;

    public BankInterfaceDescription(URL url, XS2AStandardInterface standard)
    {
        this.url = url;
        this.standard = standard;
    }

    public URL getUrl()
    {
        return url;
    }

    public XS2AStandardInterface getStandard()
    {
        return standard;
    }

}
