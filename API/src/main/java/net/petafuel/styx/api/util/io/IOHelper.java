package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerPIS;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;

public class IOHelper {
    private IOHelper() {
    }

    /**
     * Set the payment product to xml or json depending on the implementer options
     *
     * @param io               which IO Number should be checked
     * @param ioInputContainer this reference will be updated with the new Payment Product
     * @throws StyxException if the payment product is neither supported for xml nor for json
     */
    public static void processPaymentProduct(String io, IOInputContainerPIS ioInputContainer) {
        if (ioInputContainer.getIoParser().getOption(io, IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            ioInputContainer.setPaymentProduct(PaymentProduct.byValue(IOInputContainerPIS.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()));
        } else if (!ioInputContainer.getIoParser().getOption(io, ioInputContainer.getPaymentProduct().getValue()).getAsBoolean()) {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support " + ioInputContainer.getPaymentService().getValue() + " with payment-product " + ioInputContainer.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
    }
}
