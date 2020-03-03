package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;

public class PaymentRequestHelper {
    private PaymentRequestHelper(){}
    public static void buildFetchRequest(String io, IOInputContainerPIS ioInputContainer) {
        PaymentProduct paymentProduct;
        if (ioInputContainer.getIoParser().getOption(io, ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            paymentProduct = ioInputContainer.getPaymentProduct();
        } else if (ioInputContainer.getIoParser().getOption(io, IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            paymentProduct = PaymentProduct.byValue(IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue());
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support fetching " + ioInputContainer.getPaymentService().getValue() + " with payment-product " + ioInputContainer.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
        ioInputContainer.setPaymentRequest(new ReadPaymentRequest(ioInputContainer.getPaymentService(), paymentProduct, ioInputContainer.getPaymentId(), ioInputContainer.getPsu()));
    }

    public static void buildStatusRequest(String io, IOInputContainerPIS ioInputContainer) {
        PaymentProduct paymentProduct;
        if (ioInputContainer.getIoParser().getOption(io, ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            paymentProduct = ioInputContainer.getPaymentProduct();
        } else if (ioInputContainer.getIoParser().getOption(io, IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            paymentProduct = PaymentProduct.byValue(IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue());
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support status request" + ioInputContainer.getPaymentService().getValue() + " with payment-product " + ioInputContainer.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
        ioInputContainer.setPaymentRequest(new ReadPaymentStatusRequest(ioInputContainer.getPaymentService(), paymentProduct, ioInputContainer.getPaymentId()));
    }
}
