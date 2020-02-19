package net.petafuel.styx.api.v1.payment.entity;

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
    }

    @PathParam("paymentService")
    public void setPaymentService(String paymentService) {
        this.paymentService = PaymentService.byValue(paymentService);
    }
}
