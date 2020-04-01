package net.petafuel.styx.api.v1.auth.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.models.AccessToken;

import java.util.UUID;

public class AuthHandler {

    public JsonElement createAccessToken(UUID masterTokenId) {

        PersistentAccessToken persistentAccessToken = new PersistentAccessToken();
        UUID accessTokenId = UUID.randomUUID();
        AccessToken accessToken = persistentAccessToken.create(masterTokenId, accessTokenId);
        JsonObject response = new JsonObject();
        response.addProperty("token", accessToken.getId().toString());

        return response;
    }
}
