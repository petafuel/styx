package net.petafuel.styx.api.v1.authentication.boundary;

import net.petafuel.styx.api.PropertyReader;
import net.petafuel.styx.api.v1.authentication.control.AuthenticationHandler;
import net.petafuel.styx.api.v1.authentication.control.TokenGenerator;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.models.AccessToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.json.JsonObject;
import java.security.NoSuchAlgorithmException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenHashTest {
    private static String masterToken;

    @BeforeAll
    static void configure() {
        new PropertyReader().loadProperties();
        masterToken = System.getProperty("test.token.master");
        if (masterToken == null) {
            Assertions.fail("test.token.master has to be set to a valid master token hash in the test resource for API");
        }
    }

    @Test
    @Tag("integration")
    public void testTokenHashCheck() throws NoSuchAlgorithmException {
        //  handle the request
        JsonObject element = AuthenticationHandler.createAccessToken(TokenGenerator.hashSHA256(masterToken), AccessToken.ServiceType.AISPIS, 300);

        // expect that there is a "token" in the response and it's a valid UUID
        String accessTokenString = element.getString("token");
        AccessToken accessToken = PersistentAccessToken.get(TokenGenerator.hashSHA256(accessTokenString));
        Assertions.assertEquals(accessToken.getId(), TokenGenerator.hashSHA256(accessTokenString));
    }
}
