package net.petafuel.styx.core.xs2a.oauth.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RefreshTokenRequest extends OAuthTokenRequest {

    private String refreshToken;

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
        setGrantType("refresh_token");
        setJsonBody(false);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Optional<String> getRawBody() {
        String rawBody;
        if (isJsonBody()) {
            Gson gson = new GsonBuilder().registerTypeAdapter(RefreshTokenRequest.class, new TokenSerializer()).create();
            rawBody = gson.toJson(this);
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", getGrantType());
            params.put("client_id", getClientId());
            params.put("refresh_token", getRefreshToken());
            rawBody = BasicService.httpBuildQuery(params).substring(1);
        }
        return Optional.ofNullable(rawBody);
    }
}
