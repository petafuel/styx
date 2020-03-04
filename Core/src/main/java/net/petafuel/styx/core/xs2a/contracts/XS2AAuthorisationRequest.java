package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

public abstract class XS2AAuthorisationRequest extends XS2ARequest {
    // "/v1/{payment-service}/{payment.product}/{paymentId}/authorisations/{authorisationId}"
    public static final String PIS_UPDATE_PSU_DATA = "v1/%s/%s/%s/authorisations/%s";
    // "/v1/consents/{consentId}/authorisations/{authorsationId}"
    public static final String CS_UPDATE_PSU_DATA = "v1/consents/%s/authorisations/%s";
    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;
    private boolean isPIS;

    public XS2AAuthorisationRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
        setPIS(true);
    }

    public XS2AAuthorisationRequest(String consentId) {
        setConsentId(consentId);
        setPIS(false);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isPIS() {
        return isPIS;
    }

    public void setPIS(boolean isPis) {
        this.isPIS = isPis;
    }

    public abstract String getServiceURL();
}
