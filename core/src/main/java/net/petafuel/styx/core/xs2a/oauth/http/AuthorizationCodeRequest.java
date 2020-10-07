package net.petafuel.styx.core.xs2a.oauth.http;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.utils.Config;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthorizationCodeRequest extends OAuthTokenRequest {
    @JsonbProperty("code")
    private String code;
    @JsonbProperty("code_verifier")
    private String codeVerifier;
    @JsonbProperty("redirect_uri")
    private String redirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");

    public AuthorizationCodeRequest(String code, String codeVerifier) {
        this.code = code;
        this.codeVerifier = codeVerifier;
        setGrantType("authorization_code");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public Optional<String> getRawBody() {
        String rawBody;
        if (isJsonBody()) {
            try (Jsonb jsonb = JsonbBuilder.create()) {
                rawBody = jsonb.toJson(this);
            } catch (Exception e) {
                throw new SerializerException("Unable to create request body for AuthorizationCodeRequest", e);
            }
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", getGrantType());
            params.put("client_id", getClientId());
            params.put("code", getCode());
            params.put("code_verifier", getCodeVerifier());
            params.put("redirect_uri", getRedirectUri());
            rawBody = BasicService.httpBuildQuery(params).substring(1);
        }
        return Optional.ofNullable(rawBody);
    }

    @Override
    public String getServicePath() {
        return null;
    }
}
