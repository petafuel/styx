package net.petafuel.styx.core.persistence.models;

import javax.json.bind.annotation.JsonbProperty;

public class MasterTokenRestriction {

    private Integer maxUsages;

    public Integer getMaxUsages() {
        return maxUsages;
    }

    @JsonbProperty("max-usages")
    public void setMaxUsages(Integer maxUsages) {
        this.maxUsages = maxUsages;
    }

    public boolean isValid(){
        return maxUsages != null;
    }
}
