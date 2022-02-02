package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Frequency;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

/**
 * Change the frequency for periodic payments to the full written name instead of the 4 letter iso20022 code
 */
public class STYX01 extends ApplicableImplementerOption {
    private static final String IO = "STYX01";

    public STYX01(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        //Only apply if service is periodic payment and if target request is a payment initiation
        if (ioInput.getPaymentService() != PaymentService.PERIODIC_PAYMENTS || ioInput.getPayment() == null) {
            return false;
        }
        Boolean aspspUsesFrequencyName = ioParser.getOption(IO, IOParser.Option.REQUIRED);
        if (aspspUsesFrequencyName != null && aspspUsesFrequencyName) {
            String frequency = ((PeriodicPayment) ioInput.getPayment()).getFrequency();
            if (frequency.length() == 4) {
                ((PeriodicPayment) ioInput.getPayment()).setFrequency(Frequency.valueOf(frequency).getValue());
            }
        }
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
