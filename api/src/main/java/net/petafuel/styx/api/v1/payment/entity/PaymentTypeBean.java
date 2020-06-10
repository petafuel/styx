package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.ws.rs.PathParam;

public class PaymentTypeBean {
    PaymentProduct paymentProduct;
    PaymentService paymentService;

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    @PathParam("paymentProduct")
    public void setPaymentProduct(String paymentProduct) {
        this.paymentProduct = PaymentProduct.byValue(paymentProduct);
        if(this.paymentProduct == null && paymentProduct != null){
            throw new StyxException(new ResponseEntity("Unsupported paymentProduct",ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
    }

    @PathParam("paymentService")
    public void setPaymentService(String paymentService) {
        this.paymentService = PaymentService.byValue(paymentService);
        if(this.paymentService == null && paymentService != null){
            throw new StyxException(new ResponseEntity("Unsupported paymentService",ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
        }
    }
}
