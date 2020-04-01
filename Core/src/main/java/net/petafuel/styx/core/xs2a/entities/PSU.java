package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import java.io.Serializable;

/**
 * PSU model for xs2a users
 * <p>
 * <b>P</b>ayment <b>S</b>ervice <b>U</b>ser
 * </p>
 */
public class PSU implements Serializable {

    @XS2AHeader(XS2AHeader.PSU_ID)
    private String id;

    @XS2AHeader(XS2AHeader.PSU_ID_TYPE)
    private String idType;

    @XS2AHeader(XS2AHeader.PSU_CORPORATE_ID)
    private String corporateId;

    @XS2AHeader(XS2AHeader.PSU_CORPORATE_ID_TYPE)
    private String corporateIdType;

    @XS2AHeader(XS2AHeader.PSU_IP_ADDRESS)
    private String ip;

    @XS2AHeader(XS2AHeader.PSU_IP_PORT)
    private Integer port;

    @XS2AHeader(XS2AHeader.PSU_USER_AGENT)
    private String userAgent;

    @XS2AHeader(XS2AHeader.PSU_GEO_LOCATION)
    private String geoLocation;

    public PSU(String id) {
        this.id = id;
    }

    public PSU() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getCorporateIdType() {
        return corporateIdType;
    }

    public void setCorporateIdType(String corporateIdType) {
        this.corporateIdType = corporateIdType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }
}
