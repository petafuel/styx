package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.IOHelper;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
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
    public void apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (ioInput.getPaymentService() != PaymentService.PAYMENTS) {
            return;
        }
        IOHelper.processPaymentProduct(ioParser, IO, ioInput);
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
