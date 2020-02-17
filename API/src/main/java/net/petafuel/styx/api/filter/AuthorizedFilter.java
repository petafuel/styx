package net.petafuel.styx.api.filter;

import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;
import net.petafuel.styx.core.persistence.models.AccessToken;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@CheckAccessToken
@Priority(Priorities.AUTHORIZATION)
public class AuthorizedFilter extends AbstractTokenFilter {

    @Override
    public boolean checkToken(UUID uuid) {
        AccessToken accessToken = new PersistentAccessToken().get(uuid);
        return accessToken.isValid() && new PersistentClientApp().get(accessToken.getClientMasterToken()).isEnabled();
    }
}
