package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<ImplementerOption> implementerOptions;

    public Config() {
        implementerOptions = new ArrayList<>();
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

    public List<ImplementerOption> getImplementerOptions() {
        return implementerOptions;
    }

    public void setImplementerOptions(List<ImplementerOption> implementerOptions) {
        this.implementerOptions = implementerOptions;
    }
}
