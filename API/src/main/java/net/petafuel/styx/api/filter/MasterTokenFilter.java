package net.petafuel.styx.api.filter;

import net.petafuel.styx.core.persistence.layers.PersistentClientApp;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@CheckMasterToken
@Priority(Priorities.AUTHORIZATION)
public class MasterTokenFilter extends AbstractTokenFilter {

    @Override
    public boolean checkToken(UUID uuid) {
        return new PersistentClientApp().get(uuid).isEnabled();
    }
}
