package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

public abstract class SCARequest extends XS2ARequest {
    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;
    private String consentId;
    private String scaAuthenticationData;
    private String authorisationMethodId;
    private PSUData psuData;
    private boolean isPIS;

    protected SCARequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
        setPIS(true);
    }

    protected SCARequest(String consentId) {
        this.consentId = consentId;
        setPIS(false);
    }

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
    }

    public String getAuthorisationMethodId() {
        return authorisationMethodId;
    }

    public void setAuthorisationMethodId(String authorisationMethodId) {
        this.authorisationMethodId = authorisationMethodId;
    }

    public String getScaAuthenticationData() {
        return scaAuthenticationData;
    }

    public void setScaAuthenticationData(String scaAuthenticationData) {
        this.scaAuthenticationData = scaAuthenticationData;
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

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }
}
