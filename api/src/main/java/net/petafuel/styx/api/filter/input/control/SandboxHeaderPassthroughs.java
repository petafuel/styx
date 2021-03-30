package net.petafuel.styx.api.filter.input.control;

import net.petafuel.styx.api.rest.StyxFilterPriorites;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.HashMap;
import java.util.Map;

@Priority(StyxFilterPriorites.XS2ASTANDARD_DEPENDENT)
public class SandboxHeaderPassthroughs implements ContainerRequestFilter {
    private static final String HEADER_PREFIX = "X-STYX-";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        Map<String, String> sandboxHeaders = new HashMap<>();
        containerRequestContext.getHeaders().forEach((key, value) -> {
            if (key.startsWith(HEADER_PREFIX)) {
                sandboxHeaders.put(key.replace(HEADER_PREFIX, ""), value.get(0));
            }
        });
        containerRequestContext.setProperty(SandboxHeaderPassthroughs.class.getName(), sandboxHeaders);
    }
}
