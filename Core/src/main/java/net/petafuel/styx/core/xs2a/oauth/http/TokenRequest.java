package net.petafuel.styx.core.xs2a.oauth.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class TokenRequest extends XS2ARequest {

    private String code;
    private String grantType = "authorization_code";
    private String clientId = Config.getInstance().getProperties().getProperty("keystore.client_id");
    private String codeVerifier;
    private String redirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");
    private boolean jsonBody = true;

    public TokenRequest(String code, String codeVerifier) {
        this.code = code;
        this.codeVerifier = codeVerifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public boolean isJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(boolean jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public Optional<String> getRawBody() {
        String rawBody;
        if (jsonBody) {
            Gson gson = new GsonBuilder().registerTypeAdapter(TokenRequest.class, new TokenSerializer()).create();
            rawBody = gson.toJson(this);
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", grantType);
            params.put("client_id", clientId);
            params.put("code", code);
            params.put("code_verifier", codeVerifier);
            params.put("redirect_uri", redirectUri);
            rawBody = BasicService.httpBuildQuery(params).substring(1);
        }

        return Optional.ofNullable(rawBody);
    }
}
