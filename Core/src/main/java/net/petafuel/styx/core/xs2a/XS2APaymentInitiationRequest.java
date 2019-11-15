package net.petafuel.styx.core.xs2a;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class XS2APaymentInitiationRequest implements XS2ARequest {

    /** Headers */
    @XS2AHeader(nested = true)
    private PSU psu;

    @XS2AHeader(XS2AHeader.X_REQUEST_ID)
    private String xRequestId;

    @XS2AHeader(XS2AHeader.TPP_REDIRECT_PREFERRED)
    private boolean tppRedirectPreferred;

    @XS2AHeader(XS2AHeader.TPP_REDIRECT_URL)
    private String tppRedirectUri;

    @XS2AHeader(XS2AHeader.DATE)
    private String date;

    /** Accumulated Headers */
    private LinkedHashMap<String, String> headers = new LinkedHashMap<>();

    private PaymentProduct paymentProduct;
    private PaymentService paymentService;

    public XS2APaymentInitiationRequest(PaymentProduct product, PaymentService paymentService, PSU psu) {
        this.paymentProduct = product;
        this.paymentService = paymentService;
        this.psu = psu;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
        this.xRequestId = String.valueOf(UUID.randomUUID());
        this.tppRedirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.xRequestId;

    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public LinkedHashMap<String, String> getHeaders() {
        return this.headers;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }

    public String getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(String xRequestId) {
        this.xRequestId = xRequestId;
    }

    public boolean isTppRedirectPreferred() {
        return tppRedirectPreferred;
    }

    public void setTppRedirectPreferred(boolean tppRedirectPreferred) {
        this.tppRedirectPreferred = tppRedirectPreferred;
    }

    public String getTppRedirectUri() {
        return tppRedirectUri;
    }

    public void setTppRedirectUri(String tppRedirectUri) {
        this.tppRedirectUri = tppRedirectUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
