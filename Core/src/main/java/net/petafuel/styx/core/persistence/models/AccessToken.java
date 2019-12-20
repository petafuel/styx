package net.petafuel.styx.core.persistence.models;

import java.util.Date;
import java.util.UUID;

public class AccessToken {

    private UUID id;
    private UUID clientMasterToken;
    private boolean valid;
    private Date createdAt;
    private Date updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientMasterToken() {
        return clientMasterToken;
    }

    public void setClientMasterToken(UUID clientMasterToken) {
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
}
