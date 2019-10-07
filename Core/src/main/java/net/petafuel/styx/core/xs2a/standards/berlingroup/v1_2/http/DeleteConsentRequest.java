package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DeleteConsentRequest implements XS2ARequest {
    /**
     * Headers
     */
    @XS2AHeader("x-request-id")
    private String xRequestId;

    @XS2AHeader("date")
    private String date;

    @XS2AHeader("consentId")
    private String consentId;

    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    /**
     * Body
     */

    public DeleteConsentRequest() {
        this.headers = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MM yyyy HH:mm:ss zz");
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
        return this.headers;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }
}
