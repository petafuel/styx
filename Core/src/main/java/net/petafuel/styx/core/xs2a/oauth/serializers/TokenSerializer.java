package net.petafuel.styx.core.xs2a.oauth.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;

import java.lang.reflect.Type;
import java.util.Date;

public class TokenSerializer implements JsonDeserializer<OAuthSession>, JsonSerializer<TokenRequest> {

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
    public JsonElement serialize(TokenRequest tokenRequest, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty(GRANT_TYPE, tokenRequest.getGrantType());
        object.addProperty(CODE, tokenRequest.getCode());
        object.addProperty(CLIENT_ID, tokenRequest.getClientId());
        object.addProperty(CODE_VERIFIER, tokenRequest.getCodeVerifier());
        object.addProperty(REDIRECT_URI, tokenRequest.getRedirectUri());
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
             Date date = new Date();
             date.setTime(date.getTime() + seconds * 1000);
             session.setExpiresAt(date);
        }
        if (object.has(SCOPE)) {
            String scope = object.get(SCOPE).getAsString();
            session.setScope(scope);
        }
        return session;
    }
}
