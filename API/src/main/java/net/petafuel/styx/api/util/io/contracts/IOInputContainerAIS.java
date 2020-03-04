package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;

public class IOInputContainerAIS extends IOInputContainer {
    XS2ARequest aisRequest;

    protected IOInputContainerAIS(XS2AStandard xs2AStandard, PSU psu) {
        super(xs2AStandard, psu);
    }

    public XS2ARequest getAisRequest() {
        return aisRequest;
    }

    public void setAisRequest(XS2ARequest aisRequest) {
        if(this.aisRequest != null){
            //prevent implementer options from overriding an already defined request object
            throw new IllegalStateException("Request object was already created by ApplicableImplementerOption, overriding is not allowed");
        }

        this.aisRequest = aisRequest;
    }
}
