package net.petafuel.styx.core.xs2a.standards.ing.v1_0.http;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccessTokenRequest extends XS2ARequest {

    @JsonbProperty("grant_type")
    private final String grantType;

    public AccessTokenRequest() {
        this.grantType = "client_credentials";
        //add request target header to include it in signature while signing
        this.addHeader(XS2AHeader.REQUEST_TARGET, "post /oauth2/token");
    }

    @Override
    public Optional<String> getRawBody() {
        String rawBody;
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", getGrantType());
        rawBody = BasicService.httpBuildQuery(params).substring(1);
        return Optional.of(rawBody);
    }

    @Override
    public BasicService.RequestType getHttpMethod() {
        return BasicService.RequestType.POST;
    }

    @Override
    public String getServicePath() {
        return "/oauth2/token";
    }

    public String getGrantType() {
        return grantType;
    }
}
