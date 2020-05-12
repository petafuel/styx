package net.petafuel.styx.api.util.io.contracts;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;

public class IOInputContainerAIS extends IOInputContainer {
    public IOInputContainerAIS(XS2AStandard xs2AStandard, PSU psu) {
        super(xs2AStandard, psu);
    }

    public IOInputContainerAIS(RequestType requestType, XS2AStandard xs2AStandard, PSU psu, String consentId) {
        super(xs2AStandard, psu);
        buildXS2ARequest(requestType, consentId);
    }


    private void buildXS2ARequest(RequestType requestType, String consentId) {
        if (RequestType.FETCH.equals(requestType)) {
            xs2ARequest = new GetConsentRequest();
            xs2ARequest.setConsentId(consentId);
        } else if (RequestType.CREATE.equals(requestType)) {
            // TODO CreateConsentRequest
        } else {
            xs2ARequest = new StatusConsentRequest();
            xs2ARequest.setConsentId(consentId);
        }
    }

    public enum RequestType {
        CREATE,
        FETCH,
        STATUS
    }
}
