package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.IOHelper;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

/**
 * modify payment product for payment service bulk-payments
 */
public class IO3 extends ApplicableImplementerOption {
    private static final String IO = "IO3";

    public IO3(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public void apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest) throws ImplementerOptionException {
        if (ioInput.getPaymentService() != PaymentService.BULK_PAYMENTS) {
            return;
        }

        IOHelper.processPaymentProduct(ioParser, IO, ioInput);
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
