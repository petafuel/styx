package net.petafuel.styx.api.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.HashMap;
import java.util.Map;

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
