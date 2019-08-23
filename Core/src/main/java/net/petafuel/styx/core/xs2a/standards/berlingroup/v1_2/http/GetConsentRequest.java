package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GetConsentRequest implements XS2ARequest {

    /**
     * Headers
     */
    @XS2AHeader("x-request-id")
    private String xRequestId;

    @XS2AHeader("date")
    private String date;

    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    /**
     * Path Parameter
     */
    private String consentId;

    public GetConsentRequest() {
        this.headers = new LinkedHashMap<>();
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

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }
}
