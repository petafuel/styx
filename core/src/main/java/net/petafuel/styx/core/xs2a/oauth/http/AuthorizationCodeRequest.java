package net.petafuel.styx.core.xs2a.oauth.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthorizationCodeRequest extends OAuthTokenRequest {

    private String code;
    private String codeVerifier;
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
            Gson gson = new GsonBuilder().registerTypeAdapter(AuthorizationCodeRequest.class, new TokenSerializer()).create();
            rawBody = gson.toJson(this);
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
