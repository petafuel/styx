package net.petafuel.styx.core.xs2a.contracts;

import java.util.Map;

public interface XS2ARequest {
    String getRawBody();

    void setHeader(String key, String value);

    Map<String, String> getHeaders();
}
