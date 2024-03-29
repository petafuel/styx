package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

/**
 * Adds a new X-BIC Header containing the current aspsp BIC to the request,
 * before being fired
 */
public class STYX04 extends ApplicableImplementerOption {
    private static final String HEADER_X_BIC = "X-BIC";
    private static final String IO = "STYX04";

    public STYX04(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse)
            throws ImplementerOptionException {
        Boolean optionRequired = ioParser.getOption(IO, IOParser.Option.REQUIRED);
        // return immediately if this options is not required
        if (optionRequired == null || !optionRequired) {
            return false;
        }
        xs2ARequest.addHeader(HEADER_X_BIC, ioParser.getAspsp().getBic());
        if (xs2ARequest.getPsu() != null) {
            xs2ARequest.getPsu().setId(null);
        }
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
