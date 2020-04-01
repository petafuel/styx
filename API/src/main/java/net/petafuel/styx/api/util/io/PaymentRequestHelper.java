package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;

public class PaymentRequestHelper {
    private PaymentRequestHelper() {
    }

    public static void buildFetchRequest(IOInputContainerPIS ioInputContainer) {
        ioInputContainer.setPaymentRequest(new ReadPaymentRequest(ioInputContainer.getPaymentService(), ioInputContainer.getPaymentProduct(), ioInputContainer.getPaymentId(), ioInputContainer.getPsu()));
    }

    public static void buildStatusRequest(IOInputContainerPIS ioInputContainer) {
        ioInputContainer.setPaymentRequest(new ReadPaymentStatusRequest(ioInputContainer.getPaymentService(), ioInputContainer.getPaymentProduct(), ioInputContainer.getPaymentId()));
    }
}
