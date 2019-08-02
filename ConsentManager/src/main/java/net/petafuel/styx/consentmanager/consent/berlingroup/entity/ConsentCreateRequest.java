package net.petafuel.styx.consentmanager.consent.berlingroup.entity;

import net.petafuel.styx.utils.http.XS2AHeaderParser;
import net.petafuel.styx.utils.http.XS2ARequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.LinkedHashMap;

public class ConsentCreateRequest implements XS2ARequest
{
    private LinkedHashMap<String, String> headers;
    private String rawRequestBody;

    public ConsentCreateRequest(ConsentCreateModel consentCreateModel)
    {
        this.headers = new LinkedHashMap<>();
        XS2AHeaderParser.parse(consentCreateModel, this);
        this.rawRequestBody = consentCreateModel.toJson();
    }

    @Override
    public LinkedHashMap<String, String> getHeaders()
    {
        return headers;
    }

    @Override
    public void setHeader(String key, String value)
    {
        this.headers.put(key, value);
    }

    @Override
    public RequestBody getBody()
    {
        return RequestBody.create(MediaType.parse("application/json"), this.rawRequestBody);
    }

    @Override
    public String getRawBody()
    {
        return this.rawRequestBody;
    }
}
