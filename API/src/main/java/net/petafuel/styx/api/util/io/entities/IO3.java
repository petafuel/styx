package net.petafuel.styx.api.util.io.entities;

import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.api.util.io.IOHelper;
import net.petafuel.styx.api.util.io.PaymentRequestHelper;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainer;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;

import java.util.UUID;

/**
 * create a bulk payment request
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

        if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.INITIATE) {
            if (!ioInputContainer.getPaymentProduct().isXml()) {
                ioInputContainer.setPaymentRequest(new BulkPaymentInitiationJsonRequest(ioInputContainer.getPaymentProduct(), (BulkPayment) ioInputContainer.getPayment(), ioInputContainer.getPsu()));
            } else {
                PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (BulkPayment) ioInputContainer.getPayment());
                ioInputContainer.setPaymentRequest(new PaymentInitiationPain001Request(ioInputContainer.getPaymentProduct(), PaymentService.BULK_PAYMENTS, document, ioInputContainer.getPsu()));
            }
        } else if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.FETCH) {
            PaymentRequestHelper.buildFetchRequest(ioInputContainer);
        } else if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.STATUS) {
            PaymentRequestHelper.buildStatusRequest(ioInputContainer);
        } else {
            throw new IllegalArgumentException("RequestType cannot be null on request creation");
        }
        if (ioInputContainer.getPaymentRequest() != null) {
            ioInputContainer.getAdditionalHeaders().forEach((key, value) -> ioInputContainer.getPaymentRequest().addHeader(key, value));
        }
        return ioInputContainer;
    }

    @Override
    public IOOrder order() {
        return IOOrder.CREATION;
    }
}
