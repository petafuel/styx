package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.jsepa.SEPAWriter;
import net.petafuel.jsepa.exception.SEPAWriteException;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import java.util.UUID;

public class PaymentInitiationPain001Request implements XS2ARequest {

    /** Headers */
    @XS2AHeader(XS2AHeader.X_REQUEST_ID)
    private String xRequestId;

    @XS2AHeader(XS2AHeader.TPP_REDIRECT_PREFERRED)
    private boolean tppRedirectPreferred;

    @XS2AHeader(XS2AHeader.TPP_REDIRECT_URL)
    private String tppRedirectUri;

    @XS2AHeader(nested = true)
    private PSU psu;

    @XS2AHeader(XS2AHeader.DATE)
    private String date;

    /** Accumulated Headers */
    private LinkedHashMap<String, String> headers;

    /** Body */
    private PAIN00100303Document body;
    private PaymentProduct paymentProduct;

    public PaymentInitiationPain001Request(PaymentProduct paymentProduct, PAIN00100303Document body, PSU psu) {
        this.paymentProduct = paymentProduct;

        if (this.paymentProduct .equals(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS)) {
            if (body.getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate() == null || "" .equals(body.getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate())){
                body.getCctInitiation().getPmtInfos().get(0).setRequestedExecutionDate(body.getCctInitiation().getGrpHeader().getCreationTime());
            }
        }

        this.body = body;
        this.headers = new LinkedHashMap<>();
        this.psu = psu;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
        this.xRequestId = String.valueOf(UUID.randomUUID());
    }

    @Override
    public String getRawBody() {

        SEPAWriter writer = new SEPAWriter(body);
        try {
            return new String(writer.writeSEPA());
        } catch (SEPAWriteException exception) {
            return "";
        }
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
    }

    public PAIN00100303Document getBody() {
        return body;
    }

    public void setBody(PAIN00100303Document body) {
        this.body = body;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
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

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
