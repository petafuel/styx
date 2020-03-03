package net.petafuel.styx.api.util.io.entities;

import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.io.PaymentRequestHelper;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;

import java.util.UUID;

/**
 * create a bulk payment request
 */
public class IO3 implements ApplicableImplementerOption<IOInputContainerPIS> {
    private static final String IO = "IO3";

    @Override
    public void apply(IOInputContainerPIS ioInputContainer) {
        if (ioInputContainer.getPaymentService() != PaymentService.BULK_PAYMENTS) {
            return;
        }
        if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.INITIATE) {
            if (ioInputContainer.getIoParser().getOption(IO, ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
                ioInputContainer.setPaymentRequest(new BulkPaymentInitiationJsonRequest(ioInputContainer.getPaymentProduct(), (BulkPayment) ioInputContainer.getPayment(), ioInputContainer.getPsu()));
            } else if (ioInputContainer.getIoParser().getOption(IO, IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
                PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (BulkPayment) ioInputContainer.getPayment());
                ioInputContainer.setPaymentRequest(new PaymentInitiationPain001Request(PaymentProduct.byValue(IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()), PaymentService.BULK_PAYMENTS, document, ioInputContainer.getPsu()));
            } else {
                throw new StyxException(new ResponseEntity("The requested ASPSP does not support bulk-payments with payment-product " + ioInputContainer.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
            }
        } else if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.FETCH) {
            PaymentRequestHelper.buildFetchRequest(IO, ioInputContainer);
        } else if (ioInputContainer.getRequestType() == IOInputContainerPIS.RequestType.STATUS) {
            PaymentRequestHelper.buildStatusRequest(IO, ioInputContainer);
        } else {
            throw new IllegalArgumentException("RequestType cannot be null on request creation");
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.CREATION;
    }
}
