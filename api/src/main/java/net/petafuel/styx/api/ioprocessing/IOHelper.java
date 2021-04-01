package net.petafuel.styx.api.ioprocessing;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

public class IOHelper {
    private IOHelper() {
    }

    /**
     * Set the payment product to json or xml depending on the implementer options
     *
     * @param io               which IO Number should be checked
     * @param ioInputContainer this reference will be updated with the new Payment Product
     * @throws StyxException if the payment product is neither supported for xml nor for json
     */
    public static void processPaymentProduct(IOParser ioParser, String io, XS2AFactoryInput ioInputContainer) throws ImplementerOptionException {
        if (Boolean.TRUE.equals(ioParser.getOption(io, ioInputContainer.getPaymentProduct().getValue()))) {
            ioInputContainer.setPaymentProduct(PaymentProduct.byValue(ioInputContainer.getPaymentProduct().getValue()));
        } else if (Boolean.TRUE.equals(ioParser.getOption(io, IOProcessor.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()))) {
            ioInputContainer.setPaymentProduct(PaymentProduct.byValue(IOProcessor.XML_PAYMENT_PRODUCT_PREFIX + ioInputContainer.getPaymentProduct().getValue()));
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support " + ioInputContainer.getPaymentService().getValue() + " with payment-product " + ioInputContainer.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
    }
}
