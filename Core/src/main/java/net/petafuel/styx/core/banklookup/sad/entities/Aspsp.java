package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;
import net.petafuel.styx.core.persistence.DatabaseColumnOverride;

import java.util.Date;

/**
 * aspsp database row model
 */
public class Aspsp {
    @DatabaseColumn("id")
    private Integer id;
    @DatabaseColumn("name")
    private String name;
    @DatabaseColumn("bic")
    private String bic;
    @DatabaseColumn("active")
    private Boolean active;
    @DatabaseColumn("updated_at")
    private Date updatedAt;
    @DatabaseColumn("created_at")
    private Date createdAt;
    @DatabaseColumn("documentation_url")
    private String documentationUrl;

    @DatabaseColumn(value = "", nested = true)
    private AspspGroup aspspGroup;

    @DatabaseColumn(value = "", nested = true, overrides = {
            @DatabaseColumnOverride(original = "id", replacement = "production_url_id"),
            @DatabaseColumnOverride(original = "url", replacement = "production_url"),
            @DatabaseColumnOverride(original = "ais_url", replacement = "production_ais_url"),
            @DatabaseColumnOverride(original = "pis_url", replacement = "production_pis_url"),
            @DatabaseColumnOverride(original = "piis_url", replacement = "production_piis_url"),
            @DatabaseColumnOverride(original = "updated_at", replacement = "production_url_updated_at"),
            @DatabaseColumnOverride(original = "created_at", replacement = "production_url_created_at")
    })
    private Url productionUrl;

    @DatabaseColumn(value = "", nested = true, overrides = {
            @DatabaseColumnOverride(original = "id", replacement = "sandbox_url_id"),
            @DatabaseColumnOverride(original = "url", replacement = "sandbox_url"),
            @DatabaseColumnOverride(original = "ais_url", replacement = "sandbox_ais_url"),
            @DatabaseColumnOverride(original = "pis_url", replacement = "sandbox_pis_url"),
            @DatabaseColumnOverride(original = "piis_url", replacement = "sandbox_piis_url"),
            @DatabaseColumnOverride(original = "updated_at", replacement = "sandbox_url_updated_at"),
            @DatabaseColumnOverride(original = "created_at", replacement = "sandbox_url_created_at")
    })
    private Url sandboxUrl;

    @DatabaseColumn(value = "", nested = true)
    private Config config;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public AspspGroup getAspspGroup() {
        return aspspGroup;
    }

    public void setAspspGroup(AspspGroup aspspGroup) {
        this.aspspGroup = aspspGroup;
    }

    public Url getProductionUrl() {
        return productionUrl;
    }

    public void setProductionUrl(Url productionUrl) {
        this.productionUrl = productionUrl;
    }

    public Url getSandboxUrl() {
        return sandboxUrl;
    }

    public void setSandboxUrl(Url sandboxUrl) {
        this.sandboxUrl = sandboxUrl;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
