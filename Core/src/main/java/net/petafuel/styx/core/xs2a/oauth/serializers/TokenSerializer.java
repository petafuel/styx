package net.petafuel.styx.core.xs2a.oauth.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import net.petafuel.styx.core.xs2a.oauth.entities.Token;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;

import java.lang.reflect.Type;

public class TokenSerializer implements JsonDeserializer<Token>, JsonSerializer<TokenRequest> {

    public static final String GRANT_TYPE = "grant_type";
    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CODE_VERIFIER = "code_verifier";
    public static final String REDIRECT_URI = "redirect_uri";

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
    public Token deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        String accessToken = object.get("access_token").getAsString();
        String tokenType = object.get("token_type").getAsString();
        String refreshToken = object.get("refresh_token").getAsString();
        int expiresIn = object.get("expires_in").getAsInt();

        return new Token(accessToken, tokenType, refreshToken, expiresIn);
    }
}
