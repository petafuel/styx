package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.PSU;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class StatusConsentRequest extends GetConsentRequest {
    /**
     * Headers
     */
    @XS2AHeader(XS2AHeader.X_REQUEST_ID)
    private String xRequestId;

    @XS2AHeader(XS2AHeader.DATE)
    private String date;

    @XS2AHeader(XS2AHeader.CONSENT_ID)
    private String consentId;

    @XS2AHeader(nested = true)
    private PSU psu;

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

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }
}
