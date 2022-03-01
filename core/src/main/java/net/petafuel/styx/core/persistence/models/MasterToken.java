package net.petafuel.styx.core.persistence.models;

import java.util.Date;
import java.util.Map;

/**
 * This model holds the styx mastertoken + metadata
 */
public class MasterToken {
    private String token;
    private String name;
    private String redirectUrl;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;
    private Map<String, MasterTokenRestriction> restrictions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Map<String, MasterTokenRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<String, MasterTokenRestriction> restrictions) {
        this.restrictions = restrictions;
    }
}
