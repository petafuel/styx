package net.petafuel.styx.core.persistence;

import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.UUID;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersistentOAuthSessionIntegrationTest {

    private static final String AUTHORIZATION_ENDPOINT = "https://www.example.com/authorize";
    private static final String TOKEN_ENDPOINT = "https://www.example.com/token";
    private static final String SCOPE = "scope1,scope2";
    private static final String ACCESS_TOKEN = "my_access_token_exmaple";
    private static final String TOKEN_TYPE = "my_token_type_exmaple";
    private static final String REFRESH_TOKEN = "my_refresh_token_exmaple";
    private static final Date ACCESS_TOKEN_EXPIRY_DATE = DateUtils.addMinutes(new Date(), 5);
    private static final Date REFRESH_TOKEN_EXPIRY_DATE = DateUtils.addMonths(new Date(), 3);

    private OAuthSession oAuthSession;

    @BeforeAll
    void prepare() {
        UUID xRequestId = UUID.randomUUID();
        oAuthSession = OAuthSession.start(xRequestId);
        oAuthSession.setAuthorizationEndpoint(AUTHORIZATION_ENDPOINT);
        oAuthSession.setTokenEndpoint(TOKEN_ENDPOINT);
        oAuthSession.setScope(SCOPE);
    }

    @Test
    @Order(1)
    void createOAuthSession() {
        OAuthSession fromDatabase = PersistentOAuthSession.create(oAuthSession);
        equals(fromDatabase);
    }

    @Test
    @Order(2)
    void updateOAuthSession() {
        oAuthSession.setAccessToken(ACCESS_TOKEN);
        oAuthSession.setTokenType(TOKEN_TYPE);
        oAuthSession.setRefreshToken(REFRESH_TOKEN);
        oAuthSession.setAccessTokenExpiresAt(ACCESS_TOKEN_EXPIRY_DATE);
        oAuthSession.setRefreshTokenExpiresAt(REFRESH_TOKEN_EXPIRY_DATE);
        OAuthSession fromDatabase = PersistentOAuthSession.update(oAuthSession);
        equals(fromDatabase);
    }

    @Test
    @Order(3)
    void getOAuthSession() {
        OAuthSession oAuthSessionById = PersistentOAuthSession.getById(oAuthSession.getId());
        equals(oAuthSessionById);
        OAuthSession oAuthSessionByState = PersistentOAuthSession.getByState(oAuthSession.getState());
        equals(oAuthSessionByState);
        OAuthSession oAuthSessionByXRequestId = PersistentOAuthSession.getByXRequestId(oAuthSession.getxRequestId());
        equals(oAuthSessionByXRequestId);
    }

    private void equals(OAuthSession fromDatabase) {
        Assert.assertNotNull(fromDatabase);
        Assert.assertEquals(oAuthSession.getxRequestId().toString(), fromDatabase.getxRequestId().toString());
        Assert.assertEquals(oAuthSession.getAuthorizationEndpoint(), fromDatabase.getAuthorizationEndpoint());
        Assert.assertEquals(oAuthSession.getTokenEndpoint(), fromDatabase.getTokenEndpoint());
        Assert.assertEquals(oAuthSession.getCodeVerifier(), fromDatabase.getCodeVerifier());
        Assert.assertEquals(oAuthSession.getState(), fromDatabase.getState());
        Assert.assertEquals(oAuthSession.getScope(), fromDatabase.getScope());
        Assert.assertEquals(oAuthSession.getAccessToken(), fromDatabase.getAccessToken());
        Assert.assertEquals(oAuthSession.getAccessTokenExpiresAt(), fromDatabase.getAccessTokenExpiresAt());
        Assert.assertEquals(oAuthSession.getRefreshToken(), fromDatabase.getRefreshToken());
        Assert.assertEquals(oAuthSession.getRefreshTokenExpiresAt(), fromDatabase.getRefreshTokenExpiresAt());
        Assert.assertEquals(oAuthSession.getTokenType(), fromDatabase.getTokenType());
    }

}
