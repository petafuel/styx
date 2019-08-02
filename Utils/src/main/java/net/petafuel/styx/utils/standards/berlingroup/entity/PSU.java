package net.petafuel.styx.utils.standards.berlingroup.entity;

import net.petafuel.styx.utils.http.XS2AHeader;

public class PSU
{
    @XS2AHeader("psu-id")
    private String id;

    @XS2AHeader("psu-id-type")
    private String idType;

    @XS2AHeader("psu-corporate-id")
    private String corporateId;

    @XS2AHeader("psu-corporate-id-type")
    private String corporateIdType;

    public PSU(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIdType()
    {
        return idType;
    }

    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    public String getCorporateId()
    {
        return corporateId;
    }

    public void setCorporateId(String corporateId)
    {
        this.corporateId = corporateId;
    }

    public String getCorporateIdType()
    {
        return corporateIdType;
    }

    public void setCorporateIdType(String corporateIdType)
    {
        this.corporateIdType = corporateIdType;
    }
}
