package net.petafuel.styx.core.persistence.models;

import java.util.Arrays;
import java.util.Date;

/**
 * This model holds data for a client/access token that is used to access the whole styx rest interface
 */
public class AccessToken {
    private String id;
    private String clientMasterToken;
    private boolean valid;
    private Date createdAt;
    private Date updatedAt;
    private ServiceType service;
    private int expiresIn;
    private Date lastUsedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientMasterToken() {
        return clientMasterToken;
    }

    public void setClientMasterToken(String clientMasterToken) {
        this.clientMasterToken = clientMasterToken;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Date getLastUsedOn() {
        return lastUsedOn;
    }

    public void setLastUsedOn(Date lastUsedOn) {
        this.lastUsedOn = lastUsedOn;
    }

    public enum ServiceType {
        AIS("ais"),
        PIS("pis"),
        PIIS("piis"),
        AISPIS("aispis");

        String value;

        ServiceType(String value) {
            this.value = value;
        }

        public static ServiceType getByString(String name) {
            return Arrays.asList(values()).parallelStream().filter(enumEntry -> enumEntry.value.equals(name)).findFirst().orElse(null);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
