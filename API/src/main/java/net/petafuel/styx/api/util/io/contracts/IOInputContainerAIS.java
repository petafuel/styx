package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;

public class IOInputContainerAIS extends IOInputContainer {
    private XS2ARequest aisRequest;

    public IOInputContainerAIS(XS2AStandard xs2AStandard, PSU psu) {
        super(xs2AStandard, psu);
    }

    public IOInputContainerAIS(RequestType requestType, XS2AStandard xs2AStandard, PSU psu, String consentId) {
        super(xs2AStandard, psu);
        buildXS2ARequest(requestType, consentId);
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

    private void buildXS2ARequest(RequestType requestType, String consentId) {
        if (RequestType.FETCH.equals(requestType)) {
            aisRequest = new GetConsentRequest();
            aisRequest.setConsentId(consentId);
        } else if (RequestType.CREATE.equals(requestType)) {
            // TODO CreateConsentRequest
        } else {
            aisRequest = new StatusConsentRequest();
            aisRequest.setConsentId(consentId);
        }
    }

    public enum RequestType {
        CREATE,
        FETCH,
        STATUS
    }
}
