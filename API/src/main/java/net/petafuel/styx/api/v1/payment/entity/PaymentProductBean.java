package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;

import javax.ws.rs.PathParam;

public class PaymentProductBean {

    PaymentProduct paymentProduct;

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    @PathParam("paymentProduct")
    public void setPaymentProduct(String paymentProduct) {
        this.paymentProduct = PaymentProduct.byValue(paymentProduct);
    }
}
