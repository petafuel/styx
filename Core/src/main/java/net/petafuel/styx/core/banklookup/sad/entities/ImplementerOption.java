package net.petafuel.styx.core.banklookup.sad.entities;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation model of one single implementer option out of database tables configs.config or standards.config_template
 */
public class ImplementerOption {
    private String id;
    private String description;
    private Map<String, JsonElement> options;

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

    public Map<String, JsonElement> getOptions() {
        return options;
    }

    public void setOptions(Map<String, JsonElement> options) {
        this.options = options;
    }

    public void addOption(String key, JsonElement value) {
        this.options.put(key, value);
    }
}
