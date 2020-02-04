package net.petafuel.styx.api.rest.entity;

import net.petafuel.styx.core.xs2a.entities.PSU;

import javax.annotation.PostConstruct;
import javax.ws.rs.HeaderParam;

public abstract class PSUResource {

    private PSU psu;

    @HeaderParam("PSU-ID")
    private String psuId;

    @HeaderParam("PSU-Geo-Location")
    private String psuGeoLocation;

    @HeaderParam("PSU-Device-ID")
    private String psuDeviceId;

    @HeaderParam("PSU-IP-Address")
    private String psuIpAddress;

    @PostConstruct
    public void init() {
        psu = new PSU(psuId);
        psu.setGeoLocation(psuGeoLocation);
        psu.setUserAgent(psuDeviceId);
        if (psuIpAddress == null || "".equals(psuIpAddress)) {
            psu.setIp("127.0.0.1");
        } else {
            psu.setIp(psuIpAddress);
        }
    }

    public PSU getPsu() {
        return psu;
    }
}
