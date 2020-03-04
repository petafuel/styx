package net.petafuel.styx.api.util.io.entities;

import com.google.gson.JsonElement;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

/**
 * Change the frequency for periodic payments to the full written name instead of the 4 letter iso20022 code
 */
public class STYX01 implements ApplicableImplementerOption<IOInputContainerPIS> {
    private static final String IO = "STYX01";

    @Override
    public void apply(IOInputContainerPIS ioInputContainer) {
        //Only apply if service is periodic payment and if target request is a payment initiation
        if (ioInputContainer.getPaymentService() != PaymentService.PERIODIC_PAYMENTS || ioInputContainer.getRequestType() != IOInputContainerPIS.RequestType.INITIATE) {
            return;
        }
        JsonElement aspspUsesFrequencyName = ioInputContainer.getIoParser().getOption(IO, IOParser.Option.REQUIRED);
        if (aspspUsesFrequencyName != null && aspspUsesFrequencyName.getAsBoolean()) {
            String frequency = ((PeriodicPayment) ioInputContainer.getPayment()).getFrequency();
            if (frequency.length() == 4) {
                ((PeriodicPayment) ioInputContainer.getPayment()).setFrequency(PeriodicPayment.Frequency.valueOf(frequency).getValue());
            }
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_CREATION;
    }
}
