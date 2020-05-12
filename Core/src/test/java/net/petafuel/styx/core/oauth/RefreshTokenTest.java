package net.petafuel.styx.core.oauth;

import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.RefreshTokenRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class RefreshTokenTest {

    @Tag("integration")
    @Test
    public void refreshTokenTest() throws BankRequestFailedException {

        String preAuthId = "cfa0cfd3-d4db-47c6-ad45-addececcfb02";

        OAuthSession session = PersistentOAuthSession.get(preAuthId);

        if (session.getAccessTokenExpiresAt().before(new Date()) && session.getRefreshTokenExpiresAt().after(new Date())) {
            System.out.println("Token has expired");
            RefreshTokenRequest request = new RefreshTokenRequest(session.getRefreshToken());
            OAuthService service = new OAuthService();
            OAuthSession refreshed = service.tokenRequest(session.getTokenEndpoint(), request);
            refreshed.setState(preAuthId);
            PersistentOAuthSession.update(refreshed);

            Assert.assertNotEquals(session.getAccessToken(), refreshed.getAccessToken());
            System.out.println("Token is refreshed");
        }
    }
}
