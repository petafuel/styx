package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
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
    public boolean apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {

        //Only apply if a single payment and if payment initiation is the target request
        if (ioInput.getPaymentService() != PaymentService.PAYMENTS || ioInput.getPayment() == null) {
            return false;
        }
        SinglePayment payment = (SinglePayment) ioInput.getPayment();
        if (payment.getRequestedExecutionDate() != null && Boolean.FALSE.equals(ioParser.getOption(IO, IOParser.Option.AVAILABLE))) {
            throw new StyxException(new ResponseEntity("ASPSP does not support future-dated payments but requestedExecutionDate was set", ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
