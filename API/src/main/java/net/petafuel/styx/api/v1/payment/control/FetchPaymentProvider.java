package net.petafuel.styx.api.v1.payment.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.v1.payment.entity.PaymentTypeBean;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;

public class FetchPaymentProvider extends PaymentProvider {
    public FetchPaymentProvider(XS2AStandard xs2AStandard, PaymentTypeBean paymentTypeBean, PSU psu) {
        super(xs2AStandard, paymentTypeBean, psu);
    }

    public ReadPaymentRequest buildFetchPaymentRequest(String paymentId) {
        IOParser ioParser = new IOParser(xs2AStandard.getAspsp());

        ReadPaymentRequest aspspRequest;
        String implementerOptionID;
        switch (paymentTypeBean.getPaymentService()) {
            case BULK_PAYMENTS:
                implementerOptionID = "IO3";
                break;
            case PERIODIC_PAYMENTS:
                implementerOptionID = "IO4";
                break;
            default:
                implementerOptionID = "IO2";
                break;
        }

        //check io for payment product by payment service
        if (ioParser.getOption(implementerOptionID, paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            //aspsp accepts json
            aspspRequest = new ReadPaymentRequest(paymentTypeBean.getPaymentService(), paymentTypeBean.getPaymentProduct(), paymentId);
        } else if (ioParser.getOption(implementerOptionID, XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()).getAsBoolean()) {
            //aspsp does not support json, use pain001.003
            aspspRequest = new ReadPaymentRequest(paymentTypeBean.getPaymentService(), PaymentProduct.byValue(XML_PAYMENT_PRODUCT_PREFIX + paymentTypeBean.getPaymentProduct().getValue()), paymentId);
        } else {
            throw new StyxException(new ResponseEntity("The requested ASPSP does not support fetching " + paymentTypeBean.getPaymentService().getValue() + " with payment-product " + paymentTypeBean.getPaymentProduct().getValue(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.ASPSP));
        }

        return aspspRequest;
    }
}
