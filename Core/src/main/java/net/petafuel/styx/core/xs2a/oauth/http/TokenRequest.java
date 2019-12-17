package net.petafuel.styx.core.xs2a.oauth.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.util.Optional;


public class TokenRequest extends XS2ARequest {

    private String code;
    private String grantType = "authorization_code";
    private String clientId = Config.getInstance().getProperties().getProperty("keystore.client_id");
    private String codeVerifier;
    private String redirectUri = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");

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

    @Override
    public Optional<String> getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TokenRequest.class, new TokenSerializer()).create();
        return Optional.ofNullable(gson.toJson(this));
    }
}
