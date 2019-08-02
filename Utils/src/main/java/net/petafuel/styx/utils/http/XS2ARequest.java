package net.petafuel.styx.utils.http;

import okhttp3.RequestBody;
import java.util.LinkedHashMap;

public interface XS2ARequest
{
    LinkedHashMap<String, String> getHeaders();

    void setHeader(String key, String value);

    RequestBody getBody();

    String getRawBody();
}
