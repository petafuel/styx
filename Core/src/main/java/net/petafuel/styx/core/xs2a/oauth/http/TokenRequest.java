package net.petafuel.styx.core.xs2a.oauth.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;
import java.util.LinkedHashMap;


public class TokenRequest implements XS2ARequest {

    private String code;
    private String grantType;
    private String clientId;
    private String codeVerifier;
    private String redirectUri;

    public TokenRequest(String code, String codeVerifier)
    {
        this.code = code;
        this.grantType = "authorization_code";
        this.clientId = Config.getInstance().getProperties().getProperty("keystore.client_id");
        this.codeVerifier = codeVerifier;
        this.redirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");
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

    @Override
    public String getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TokenRequest.class, new TokenSerializer()).create();
        return gson.toJson(this);
    }

    @Override
    public void setHeader(String key, String value) {
        // Headers are not required for this request
    }

    @Override
    public LinkedHashMap<String, String> getHeaders() {
        return new LinkedHashMap<>();
    }
}
