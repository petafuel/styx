package net.petafuel.styx.api.v1.auth.boundary;

import com.google.gson.JsonElement;
import net.petafuel.styx.api.v1.auth.control.AuthHandler;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.models.AccessToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import java.util.UUID;

public class AuthResourceTest {

    private AuthHandler cut = new AuthHandler();
    private String masterToken = System.getProperty("masterToken");

    @Test
    @Tag("integration")
    public void testAuth() {

        if (masterToken != null) {
            //  handle the request
            JsonElement element = cut.createAccessToken(UUID.fromString(masterToken));

            // expect that there is a "token" in the response and it's a valid UUID
            String accessTokenString = element.getAsJsonObject().get("token").getAsString();
            UUID accessTokenUuid = UUID.fromString(accessTokenString);

            // expect that the accessToken is stored in the database
            PersistentAccessToken persistentAccessToken = new PersistentAccessToken();
            AccessToken accessToken = persistentAccessToken.get(accessTokenUuid);
            Assert.assertEquals(accessToken.getId().toString(), accessTokenString);
        }
    }
}
