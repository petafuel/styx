package net.petafuel.styx.api.v1.consent.control;

import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerAIS;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;

public class ConsentProvider {

    protected final XS2AStandard xs2AStandard;
    protected final PSU psu;

    public ConsentProvider(XS2AStandard xs2AStandard, PSU psu) {
        this.xs2AStandard = xs2AStandard;
        this.psu = psu;
    }

    public XS2ARequest buildFetchConsentRequest(String consentId) {

        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(
                IOInputContainerAIS.RequestType.FETCH,
                xs2AStandard, psu, consentId);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);

        return ioProcessor.applyOptions();
    }

    public XS2ARequest getConsentStatusRequest(String consentId) {

        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(
                IOInputContainerAIS.RequestType.STATUS,
                xs2AStandard, psu, consentId);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);

        return ioProcessor.applyOptions();
    }
}
