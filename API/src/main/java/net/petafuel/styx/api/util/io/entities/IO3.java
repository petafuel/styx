package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.io.IOHelper;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainer;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

/**
 * modify payment product for payment service bulk-payments
 */
public class IO3 implements ApplicableImplementerOption {
    private static final String IO = "IO3";

    @Override
    public IOInputContainer apply(IOInputContainer ioInput) throws ImplementerOptionException {
        IOInputContainerPIS ioInputContainer = (IOInputContainerPIS) ioInput;
        if (ioInputContainer.getPaymentService() != PaymentService.BULK_PAYMENTS) {
            return ioInputContainer;
        }

        IOHelper.processPaymentProduct(IO, ioInputContainer);
        if (ioInputContainer.getXs2ARequest() != null) {
            ((XS2APaymentRequest) ioInputContainer.getXs2ARequest()).setPaymentProduct(ioInputContainer.getPaymentProduct());
        }
        return ioInputContainer;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
