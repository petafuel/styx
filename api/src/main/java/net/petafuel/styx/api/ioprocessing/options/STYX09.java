package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.ioprocessing.IOParser;
import net.petafuel.styx.api.ioprocessing.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.ioprocessing.contracts.IOOrder;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ASPSP requires PSU-ID-Type
 * add psuIdType header to xs2aRequest if option is true
 */
public class STYX09 extends ApplicableImplementerOption {
    private static final Logger LOG = LogManager.getLogger(STYX09.class);

    public STYX09(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {

        Boolean optionRequired = ioParser.getOption(STYX09.class.getSimpleName(), IOParser.Option.REQUIRED);
        //return immediately if this options is not required
        if (optionRequired == null || !optionRequired) {
            return false;
        }

        if (xs2AFactoryInput == null || xs2AFactoryInput.getPsu() == null) {
            LOG.error("PSU not initialized yet - cannot add psu id type header");
            return false;
        }

        String bic = ioParser.getAspsp().getBic();
        String countryCode = bic.substring(4, 6);

        String psuIdType = countryCode.equals("DE")  ? "HVB_ONLINEBANKING" : "24YOU";
        xs2ARequest.addHeader(XS2AHeader.PSU_ID_TYPE, psuIdType);
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
