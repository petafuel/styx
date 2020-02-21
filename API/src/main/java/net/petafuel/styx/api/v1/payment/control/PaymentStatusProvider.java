package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;

public class PaymentStatusProvider {

    private static final String XML_PAYMENT_PRODUCT_PREFIX = "pain.001-";

    public ReadPaymentStatusRequest buildRequest(Aspsp aspsp, PaymentService paymentService, String implementerOptionId, String paymentProduct, String paymentId) {

        IOParser ioParser = new IOParser(aspsp);
        //check implementer options for xml or json
        if (ioParser.getOption(implementerOptionId, paymentProduct).getAsBoolean()) {
            //aspsp accepts json
            return new ReadPaymentStatusRequest(paymentService, PaymentProduct.byValue(paymentProduct), paymentId);
        } else if (ioParser.getOption(implementerOptionId, XML_PAYMENT_PRODUCT_PREFIX + paymentProduct).getAsBoolean()) {
            //aspsp does not support json, use pain001.003
            return new ReadPaymentStatusRequest(paymentService, PaymentProduct.byValue(XML_PAYMENT_PRODUCT_PREFIX + paymentProduct), paymentId);
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support " + paymentService.getValue() + " with payment-product " + paymentProduct, ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }
    }
}
