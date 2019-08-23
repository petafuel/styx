package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class StatusConsentRequest extends GetConsentRequest {
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

    public StatusConsentRequest() {
        this.headers = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
    }
}
