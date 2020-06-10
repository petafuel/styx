package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;

import java.util.Date;

/**
 * urls database row model
 */
public class Url {
    @DatabaseColumn("id")
    private Integer id;
    @DatabaseColumn("url")
    private String commonUrl;
    @DatabaseColumn("ais_url")
    private String aisUrl;
    @DatabaseColumn("pis_url")
    private String pisUrl;
    @DatabaseColumn("piis_url")
    private String piisUrl;
    @DatabaseColumn("preauth_authorization_endpoint")
    private String preauthAuthorizationEndpoint;
    @DatabaseColumn("preauth_token_endpoint")
    private String preauthTokenEndpoint;
    @DatabaseColumn("updated_at")
    private Date updatedAt;
    @DatabaseColumn("created_at")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommonUrl() {
        return commonUrl;
    }

    public void setCommonUrl(String commonUrl) {
        this.commonUrl = commonUrl;
    }

    public String getAisUrl() {
        return aisUrl;
    }

    public void setAisUrl(String aisUrl) {
        this.aisUrl = aisUrl;
    }

    public String getPisUrl() {
        return pisUrl;
    }

    public void setPisUrl(String pisUrl) {
        this.pisUrl = pisUrl;
    }

    public String getPiisUrl() {
        return piisUrl;
    }

    public void setPiisUrl(String piisUrl) {
        this.piisUrl = piisUrl;
    }

    public String getPreauthAuthorizationEndpoint() {
        return preauthAuthorizationEndpoint;
    }

    public void setPreauthAuthorizationEndpoint(String preauthAuthorizationEndpoint) {
        this.preauthAuthorizationEndpoint = preauthAuthorizationEndpoint;
    }

    public String getPreauthTokenEndpoint() {
        return preauthTokenEndpoint;
    }

    public void setPreauthTokenEndpoint(String preauthTokenEndpoint) {
        this.preauthTokenEndpoint = preauthTokenEndpoint;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
