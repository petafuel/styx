package net.petafuel.styx.core.xs2a.oauth.http;


import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RefreshTokenRequest extends OAuthTokenRequest {
    @JsonbProperty("refresh_token")
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
            try (Jsonb jsonb = JsonbBuilder.create()) {
                rawBody = jsonb.toJson(this);
            } catch (Exception e) {
                throw new SerializerException("Unable to create request body for RefreshTokenRequest");
            }
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", getGrantType());
            params.put("client_id", getClientId());
            params.put("refresh_token", getRefreshToken());
            rawBody = BasicService.httpBuildQuery(params).substring(1);
        }
        return Optional.ofNullable(rawBody);
    }

    @Override
    public String getServicePath() {
        return null;
    }
}
