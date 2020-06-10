package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.utils.Config;

public abstract class PISRequest extends XS2ARequest {
    protected InitializablePayment payment;
    protected PaymentProduct paymentProduct;
    protected PaymentService paymentService;

    protected String paymentId;

    protected String multipartBoundary;

    protected PISRequest(PaymentService paymentService, PaymentProduct paymentProduct, PSU psu, InitializablePayment payment) {
        super();
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.setPsu(psu);
        this.payment = payment;
        this.setTppRedirectUri(Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.getXrequestId());
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

    public InitializablePayment getPayment() {
        return payment;
    }

    public void setPayment(InitializablePayment payment) {
        this.payment = payment;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getMultipartBoundary() {
        return multipartBoundary;
    }

    public void setMultipartBoundary(String multipartBoundary) {
        this.multipartBoundary = multipartBoundary;
    }
}
