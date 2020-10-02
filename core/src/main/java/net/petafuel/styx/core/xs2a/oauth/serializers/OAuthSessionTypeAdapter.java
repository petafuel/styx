package net.petafuel.styx.core.xs2a.oauth.serializers;

import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;

import javax.json.bind.adapter.JsonbAdapter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class OAuthSessionTypeAdapter implements JsonbAdapter<OAuthSession, OAuthSession> {
    /**
     * No need to modify to json, should remain the same
     */
    @Override
    public OAuthSession adaptToJson(OAuthSession oAuthSession) throws Exception {
        return oAuthSession;
    }

    /**
     * set refresh token expire date to 90 days by default
     */
    @Override
    public OAuthSession adaptFromJson(OAuthSession oAuthSession) throws Exception {
        Date copy = oAuthSession.getAccessTokenExpiresAt();
        oAuthSession.setRefreshTokenExpiresAt(Date.from(copy.toInstant().plus(90, ChronoUnit.DAYS)));
        return oAuthSession;
    }
}
