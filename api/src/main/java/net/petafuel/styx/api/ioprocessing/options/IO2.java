package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.ioprocessing.IOHelper;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;


/**
 * modify payment product to either xml or json for sepa-credit
 */
public class IO2 extends ApplicableImplementerOption {
    private static final String IO = "IO2";

    public IO2(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (ioInput.getPaymentService() != PaymentService.PAYMENTS) {
            return false;
        }
        IOHelper.processPaymentProduct(ioParser, IO, ioInput);
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
