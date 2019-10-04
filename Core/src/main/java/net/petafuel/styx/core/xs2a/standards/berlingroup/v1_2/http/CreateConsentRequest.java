package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CreateConsentRequest implements XS2ARequest {

    /**
     * Headers
     */
    @XS2AHeader(nested = true)
    private PSU psu;

    @XS2AHeader("x-request-id")
    private String xRequestId;

    @XS2AHeader("date")
    private String date;

    /**
     * Body
     */
    Consent consent;
    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    public CreateConsentRequest(Consent consent) {
        this.headers = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MMM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
        this.consent = consent;
        if (consent.getxRequestId() == null) {
            UUID uuid = UUID.randomUUID();
            this.xRequestId = uuid.toString();
            consent.setxRequestId(uuid);
        } else {
            this.xRequestId = consent.getxRequestId().toString();
        }
        this.setPsu(consent.getPsu());
    }

    @Override
    public String getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CreateConsentRequest.class, new ConsentSerializer()).create();
        return gson.toJson(this);
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public String getxRequestId() {
        return xRequestId;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }
}
