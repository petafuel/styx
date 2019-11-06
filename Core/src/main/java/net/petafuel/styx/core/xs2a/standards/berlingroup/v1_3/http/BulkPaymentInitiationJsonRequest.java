package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.BulkPaymentInitiationJsonRequestSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class BulkPaymentInitiationJsonRequest implements XS2ARequest {

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

    /** Body attributes */
    private List<Payment> payments;
    private PaymentProduct paymentProduct;
    private boolean batchBookingPreferred;
    private Date requestedExecutionDate;


    public BulkPaymentInitiationJsonRequest(PaymentProduct paymentProduct, List<Payment> payments, PSU psu, boolean batchBookingPreferred) {
        this.paymentProduct = paymentProduct;
        this.payments = payments;
        this.psu = psu;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
        this.xRequestId = String.valueOf(UUID.randomUUID());
        this.tppRedirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.xRequestId;
        this.batchBookingPreferred = batchBookingPreferred;
    }

    @Override
    public String getRawBody() {

        JsonSerializer serializer = new BulkPaymentInitiationJsonRequestSerializer();
        Gson gson = new GsonBuilder().registerTypeAdapter(this.getClass(), serializer).create();
        return gson.toJson(this);
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public LinkedHashMap<String, String> getHeaders() {
        return this.headers;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
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

    public boolean isBatchBookingPreferred() {
        return batchBookingPreferred;
    }

    public void setBatchBookingPreferred(boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
}
