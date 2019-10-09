package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
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

    @XS2AHeader("tpp-redirect-preferred")
    private boolean tppRedirectPreferred;

    @XS2AHeader("tpp-redirect-uri")
    private String tppRedirectUri;

    @XS2AHeader("tpp-nok-redirect-uri")
    private String tppNokRedirectUri;

    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    /**
     * Body
     */
    Consent consent;

    public CreateConsentRequest(Consent consent) {
        this.headers = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        //Maybe in some cases we need different date formats
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
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
        this.tppRedirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.xRequestId;
        this.tppNokRedirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.xRequestId;
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

    public Boolean getTppRedirectPreferred() {
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

    public String getTppNokRedirectUri() {
        return tppNokRedirectUri;
    }

    public void setTppNokRedirectUri(String tppNokRedirectUri) {
        this.tppNokRedirectUri = tppNokRedirectUri;
    }
}
