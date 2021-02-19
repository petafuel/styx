package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.PSU;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This defines the uber class for all xs2arequests
 */
public abstract class XS2ARequest {
    /**
     * Common Request Attributes
     */
    protected String authorisationId;
    /**
     * Header fields
     */
    @XS2AHeader(nested = true)
    private PSU psu;
    @XS2AHeader(XS2AHeader.X_REQUEST_ID)
    private String xrequestId;
    @XS2AHeader(XS2AHeader.DATE)
    private String date;
    @XS2AHeader(XS2AHeader.TPP_REDIRECT_PREFERRED)
    private boolean tppRedirectPreferred;
    @XS2AHeader(XS2AHeader.TPP_REDIRECT_URL)
    private String tppRedirectUri;
    @XS2AHeader(XS2AHeader.TPP_NOK_REDIRECT_URI)
    private String tppNokRedirectUri;
    @XS2AHeader(XS2AHeader.AUTHORIZATION)
    private String authorization;
    @XS2AHeader(XS2AHeader.ACCEPT)
    private String accept;
    /**
     * Aggregated Headers and Query parameters
     */
    private Map<String, String> headers;
    private Map<String, String> queryParameters;

    protected XS2ARequest() {
        psu = new PSU();
        xrequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = simpleDateFormat.format(new Date());
        headers = new LinkedHashMap<>();
        queryParameters = new LinkedHashMap<>();
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public abstract Optional<String> getRawBody();

    /**
     * add a single header to the existing header map
     *
     * @param key   Headerline key
     * @param value Headerline value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Overwrites the current header map
     *
     * @param headers map with all necessary headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * add a single queryparameter
     *
     * @param key   key for the parameter
     * @param value value for the key
     */
    public void addQueryParameter(String key, String value) {
        queryParameters.put(key, value);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    /**
     * overwrite the queryparameter Map
     *
     * @param queryParameters a new map with all necessary queryparameters
     */
    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }

    public String getXrequestId() {
        return xrequestId;
    }

    public void setXrequestId(String xrequestId) {
        if (xrequestId != null) {
            this.xrequestId = xrequestId;
        }
    }

    public String getTppRedirectUri() {
        return tppRedirectUri;
    }

    public void setTppRedirectUri(String tppRedirectUri) {
        this.tppRedirectUri = tppRedirectUri;
    }

    public String getTppNokRedirectUri() {
        return tppNokRedirectUri;
    }

    public void setTppNokRedirectUri(String tppNokRedirectUri) {
        this.tppNokRedirectUri = tppNokRedirectUri;
    }

    public boolean isTppRedirectPreferred() {
        return tppRedirectPreferred;
    }

    public void setTppRedirectPreferred(boolean tppRedirectPreferred) {
        this.tppRedirectPreferred = tppRedirectPreferred;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        if (authorization != null && !authorization.contains("Bearer")) {
            authorization = "Bearer " + authorization;
        }
        this.authorization = authorization;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public abstract String getServicePath();
}
