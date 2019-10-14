package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

/**
 * PSU model for xs2a users
 * <p>
 *     <b>P</b>ayment <b>S</b>ervice <b>U</b>ser
 * </p>
 */
public class PSU {

    @XS2AHeader("psu-id")
    private String id;

    @XS2AHeader("psu-id-type")
    private String idType;

    @XS2AHeader("psu-corporate-id")
    private String corporateId;

    @XS2AHeader("psu-corporate-id-type")
    private String corporateIdType;

    @XS2AHeader("psu-ip-address")
    private String ip;

    @XS2AHeader("psu-ip-port")
    private Integer port;

    @XS2AHeader("psu-user-agent")
    private String userAgent;

    @XS2AHeader("psu-geo-location")
    private String geoLocation;

    public PSU(String id) {
        this.id = id;
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
