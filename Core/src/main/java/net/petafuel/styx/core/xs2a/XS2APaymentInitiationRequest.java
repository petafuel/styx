package net.petafuel.styx.core.xs2a;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.utils.Config;

public abstract class XS2APaymentInitiationRequest extends XS2ARequest {

    private PaymentProduct paymentProduct;
    private PaymentService paymentService;

    public XS2APaymentInitiationRequest(PaymentProduct product, PaymentService paymentService, PSU psu) {
        super();
        this.paymentProduct = product;
        this.paymentService = paymentService;
        this.setPsu(psu);
        this.setTppRedirectUri(Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.getxRequestId());
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
