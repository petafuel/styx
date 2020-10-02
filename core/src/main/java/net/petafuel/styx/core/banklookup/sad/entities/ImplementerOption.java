package net.petafuel.styx.core.banklookup.sad.entities;


import java.util.HashMap;
import java.util.Map;

/**
 * Representation model of one single implementer option out of database tables configs.config or standards.config_template
 */
public class ImplementerOption {
    private String id;
    private String description;
    private Map<String, Boolean> options;

    public ImplementerOption() {
        options = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public void addOption(String key, Boolean value) {
        this.options.put(key, value);
    }
}
