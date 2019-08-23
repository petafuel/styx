package net.petafuel.styx.core.xs2a.contracts;

import java.util.LinkedHashMap;

public interface XS2ARequest {
    String getRawBody();

    void setHeader(String key, String value);

    LinkedHashMap<String, String> getHeaders();
}
