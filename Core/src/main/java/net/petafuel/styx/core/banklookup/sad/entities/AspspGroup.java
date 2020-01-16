package net.petafuel.styx.core.banklookup.sad.entities;

import net.petafuel.styx.core.persistence.DatabaseColumn;

import java.util.Date;

/**
 * aspsp_groups database row model
 */
public class AspspGroup {
    @DatabaseColumn("aspsp_groups_id")
    private Integer id;
    @DatabaseColumn("aspsp_groups_name")
    private String name;
    @DatabaseColumn("aspsp_groups_documentation_url")
    private String documentationUrl;
    @DatabaseColumn("aspsp_groups_updated_at")
    private Date updatedAt;
    @DatabaseColumn("aspsp_groups_created_at")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
