package net.petafuel.styx.api.filters;

import net.petafuel.styx.core.persistence.layers.PersistentClientApp;

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
       return new PersistentClientApp().get(uuid).isEnabled();
    }
}
