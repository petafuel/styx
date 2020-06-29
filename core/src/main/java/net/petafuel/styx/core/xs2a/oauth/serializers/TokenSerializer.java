package net.petafuel.styx.core.xs2a.oauth.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.AuthorizationCodeRequest;
import net.petafuel.styx.core.xs2a.oauth.http.OAuthTokenRequest;
import net.petafuel.styx.core.xs2a.oauth.http.RefreshTokenRequest;

import java.lang.reflect.Type;
import java.util.Date;

public class TokenSerializer implements JsonDeserializer<OAuthSession>, JsonSerializer<OAuthTokenRequest> {

    private static final String GRANT_TYPE = "grant_type";
    private static final String CODE = "code";
    private static final String CLIENT_ID = "client_id";
    private static final String CODE_VERIFIER = "code_verifier";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String TOKEN_TYPE = "token_type";
    private static final String EXPIRES_IN = "expires_in";
    private static final String SCOPE = "scope";

    @Override
    public JsonElement serialize(OAuthTokenRequest tokenRequest, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty(GRANT_TYPE, tokenRequest.getGrantType());
        object.addProperty(CLIENT_ID, tokenRequest.getClientId());
        if (tokenRequest instanceof AuthorizationCodeRequest) {
            AuthorizationCodeRequest request = (AuthorizationCodeRequest) tokenRequest;
            object.addProperty(CODE, request.getCode());
            object.addProperty(CODE_VERIFIER, request.getCodeVerifier());
            object.addProperty(REDIRECT_URI, request.getRedirectUri());
        } else {
            RefreshTokenRequest request = (RefreshTokenRequest) tokenRequest;
            object.addProperty(REFRESH_TOKEN, request.getRefreshToken());
        }
        return object;
    }

    @Override
    public OAuthSession deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        String accessToken = object.get(ACCESS_TOKEN).getAsString();
        String tokenType = object.get(TOKEN_TYPE).getAsString();
        OAuthSession session = new OAuthSession(accessToken, tokenType);
        if (object.has(REFRESH_TOKEN)) {
            String refreshToken = object.get(REFRESH_TOKEN).getAsString();
            session.setRefreshToken(refreshToken);
        }
        if (object.has(EXPIRES_IN)) {
             int seconds = object.get(EXPIRES_IN).getAsInt();
             Date date1 = new Date();
             date1.setTime(date1.getTime() + seconds * 1000);
             session.setAccessTokenExpiresAt(date1);
             long milliseconds = 7776000000L; // 90 days in milliseconds 90 * 24 * 60 * 60 * 1000
             Date date2 = new Date();
             date2.setTime(date2.getTime() + milliseconds);
             session.setRefreshTokenExpiresAt(date2);
        }
        if (object.has(SCOPE)) {
            String scope = object.get(SCOPE).getAsString();
            session.setScope(scope);
        }
        return session;
    }
}
