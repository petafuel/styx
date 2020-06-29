package net.petafuel.styx.core.persistence.models;

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
    private String serviceType;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

}
