package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;

import java.util.Date;

/**
 * standards database row model
 */
public class Standard {
    @DatabaseColumn("standards_id")
    private int id;
    @DatabaseColumn("standards_name")
    private String name;
    @DatabaseColumn("standards_version")
    private String version;
    @DatabaseColumn("standards_config_template")
    private String configTemplate;
    @DatabaseColumn("standards_updated_at")
    private Date updatedAt;
    @DatabaseColumn("standards_created_at")
    private Date createdAt;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}