package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;

public class StructuredAdditionalInformation {
    @JsonbProperty("standingOrderDetails")
    private StandingOrderDetail standingOrderDetail;

    public StandingOrderDetail getStandingOrderDetail() {
        return standingOrderDetail;
    }

    public void setStandingOrderDetail(StandingOrderDetail standingOrderDetail) {
        this.standingOrderDetail = standingOrderDetail;
    }
}