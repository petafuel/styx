package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Access;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentRequestSerializer;

import java.text.SimpleDateFormat;
import java.util.*;

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

    //Accumulated Headers
    private LinkedHashMap<String, String> headers;

    /**
     * Body
     */
    private Access access = new Access();
    private boolean recurringIndicator;
    private Date validUntil;
    private int frequencyPerDay;
    private boolean combinedServiceIndicator;

    public CreateConsentRequest() {
        this.headers = new LinkedHashMap<>();
        this.xRequestId = String.valueOf(UUID.randomUUID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MMM yyyy HH:mm:ss zz");
        this.date = simpleDateFormat.format(new Date());
    }

    @Override
    public String getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CreateConsentRequest.class, new ConsentRequestSerializer()).create();
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

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public boolean isRecurringIndicator() {
        return recurringIndicator;
    }

    public void setRecurringIndicator(boolean recurringIndicator) {
        this.recurringIndicator = recurringIndicator;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public int getFrequencyPerDay() {
        return frequencyPerDay;
    }

    public void setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public boolean isCombinedServiceIndicator() {
        return combinedServiceIndicator;
    }

    public void setCombinedServiceIndicator(boolean combinedServiceIndicator) {
        this.combinedServiceIndicator = combinedServiceIndicator;
    }
}
