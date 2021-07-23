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
 * ASPSP requires PSU-IP-Address
 * add psuIpAddress header to xs2aRequest if option is true
 */
public class STYX08 extends ApplicableImplementerOption {
    private static final Logger LOG = LogManager.getLogger(STYX08.class);

    public STYX08(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {

        Boolean optionRequired = ioParser.getOption(STYX08.class.getSimpleName(), IOParser.Option.REQUIRED);
        //return immediately if this options is not required
        if (optionRequired == null || !optionRequired) {
            return false;
        }

        if (xs2AFactoryInput == null || xs2AFactoryInput.getPsu() == null || xs2AFactoryInput.getPsu().getIp() == null || xs2AFactoryInput.getPsu().getIp().isEmpty()) {
            LOG.error("PSU not initialized yet - cannot add psu ip header");
            return false;
        }
        xs2ARequest.addHeader(XS2AHeader.PSU_IP_ADDRESS, xs2AFactoryInput.getPsu().getIp());
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
