package net.petafuel.styx.core.xs2a.contracts;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class XS2AGetRequest implements XS2ARequest {

    /**
     * Headers
     */
    @XS2AHeader(XS2AHeader.X_REQUEST_ID)
    private String xRequestId;

    @XS2AHeader(XS2AHeader.CONSENT_ID)
    private String consentId;

    @XS2AHeader(XS2AHeader.DATE)
    private String date;

    // Optional header. Necessary only if an OAuth2 based authentication was performed in a pre-step or in the related consent authorisation.
    @XS2AHeader(XS2AHeader.AUTHORIZATION)
    private String authorization;

    @XS2AHeader(XS2AHeader.ACCEPT)
    private String accept;

    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    //Accumulated Query Parameters
    private LinkedHashMap<String, String> queryParameters;

    /**
     *
     * @param consentId specifies the consent ID that will be retrieved from the ASPSP
     */
    public XS2AGetRequest(String consentId) {
        this.consentId = consentId;
        this.headers = new LinkedHashMap<>();
        this.queryParameters = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
    }

    /**
     * Body
     */
    public XS2AGetRequest() {
        this.headers = new LinkedHashMap<>();
        this.queryParameters = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
    }

    @Override
    public String getRawBody() {
        return "";
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public void setQueryParameter(String key, String value) {
        this.queryParameters.put(key, value);
    }

    public LinkedHashMap<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(String xRequestId) {
        this.xRequestId = xRequestId;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
