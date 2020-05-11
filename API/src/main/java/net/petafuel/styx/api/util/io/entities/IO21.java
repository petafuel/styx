package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainer;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

/**
 * Check if Bank supports future-dated payments
 */
public class IO21 implements ApplicableImplementerOption {
    private static final String IO = "IO21";

    @Override
    public IOInputContainer apply(IOInputContainer ioInput) throws ImplementerOptionException {
        IOInputContainerPIS ioInputContainer = (IOInputContainerPIS) ioInput;
        //Only apply if a single payment and if payment initiation is the target request
        if (ioInputContainer.getPaymentService() != PaymentService.PAYMENTS || ioInputContainer.getRequestType() != IOInputContainerPIS.RequestType.INITIATE) {
            return ioInputContainer;
        }
        Payment payment = (Payment) ioInputContainer.getPayment();
        if (payment.getRequestedExecutionDate() != null && !ioInputContainer.getIoParser().getOption(IO, IOParser.Option.AVAILABLE).getAsBoolean()) {
            throw new StyxException(new ResponseEntity("ASPSP does not support future-dated payments but requestedExecutionDate was set", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }

        return ioInputContainer;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
