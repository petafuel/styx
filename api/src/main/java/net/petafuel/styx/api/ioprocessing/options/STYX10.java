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
 * This Styx option is exclusively for Unicredit DE and AT
 */
public class STYX10 extends ApplicableImplementerOption {
    private static final Logger LOG = LogManager.getLogger(STYX10.class);

    public STYX10(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {

        Boolean optionRequired = ioParser.getOption(STYX10.class.getSimpleName(), IOParser.Option.REQUIRED);
        //return immediately if this options is not required
        if (optionRequired == null || !optionRequired) {
            return false;
        }

        if (xs2AFactoryInput == null || xs2AFactoryInput.getPsu() == null) {
            LOG.error("PSU not initialized yet - cannot add psu id type header");
            return false;
        }

        String bic = ioParser.getAspsp().getBic();
        if (bic.length() < 6) {
            LOG.error("BIC is not valid.");
            throw new ImplementerOptionException("BIC is not valid.");
        }
        String countryCode = bic.substring(4, 6);

        String psuIdType = "";
        if (countryCode.equals("DE")) {
            psuIdType = "HVB_ONLINEBANKING";
        } else if (countryCode.equals("AT")) {
            psuIdType = "24YOU";
        } else {
            return false;
        }
        LOG.info("countryCode={}, psuIdType={}", countryCode, psuIdType);

        xs2ARequest.addHeader(XS2AHeader.PSU_ID_TYPE, psuIdType);
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
