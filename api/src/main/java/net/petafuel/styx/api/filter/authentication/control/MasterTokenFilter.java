package net.petafuel.styx.api.filter.authentication.control;

import net.petafuel.styx.api.filter.authentication.boundary.CheckMasterToken;
import net.petafuel.styx.core.persistence.layers.PersistentClientApp;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@CheckMasterToken
@Priority(Priorities.AUTHORIZATION)
public class MasterTokenFilter extends AbstractTokenFilter {

    @Override
    public boolean checkToken(String masterToken) {
        return PersistentClientApp.get(masterToken).isEnabled();
    }
}
