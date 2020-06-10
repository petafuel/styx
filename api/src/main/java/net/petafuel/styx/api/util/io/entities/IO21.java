package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

/**
 * Check if Bank supports future-dated payments
 */
public class IO21 extends ApplicableImplementerOption {
    private static final String IO = "IO21";

    public IO21(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public void apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest) throws ImplementerOptionException {

        //Only apply if a single payment and if payment initiation is the target request
        if (ioInput.getPaymentService() != PaymentService.PAYMENTS || ioInput.getPayment() == null) {
            return;
        }
        Payment payment = (Payment) ioInput.getPayment();
        if (payment.getRequestedExecutionDate() != null && !ioParser.getOption(IO, IOParser.Option.AVAILABLE).getAsBoolean()) {
            throw new StyxException(new ResponseEntity("ASPSP does not support future-dated payments but requestedExecutionDate was set", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
