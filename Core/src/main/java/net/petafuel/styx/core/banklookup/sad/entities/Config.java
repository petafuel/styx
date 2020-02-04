package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * configs database row model
 */
public class Config {
    @DatabaseColumn("configs_id")
    private int id;
    @DatabaseColumn(value = "", nested = true)
    private Standard standard;
    @DatabaseColumn("config")
    private String configuration;
    @DatabaseColumn("configs_updated_at")
    private Date updatedAt;
    @DatabaseColumn("configs_created_at")
    private Date createdAt;

    private Map<String, ImplementerOption> implementerOptions;

    public Config() {
        implementerOptions = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public Standard getStandard() {
        return standard;
    }

    public String getConfiguration() {
        return configuration;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Map<String, ImplementerOption> getImplementerOptions() {
        return implementerOptions;
    }

    public void setImplementerOptions(Map<String, ImplementerOption> implementerOptions) {
        this.implementerOptions = implementerOptions;
    }
}
