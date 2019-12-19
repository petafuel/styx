package net.petafuel.styx.api.filters;

import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@CheckAccessToken
@Priority(Priorities.AUTHORIZATION)
public class AuthorizedFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        FilterHelper.checkToken(containerRequestContext, new PersistentAccessToken());
    }
}
