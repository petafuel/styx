package net.petafuel.styx.api.filter.output.control;

import net.petafuel.styx.api.event.RequestUUIDAdapter;
import org.apache.logging.log4j.ThreadContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ReferenceHeaderFilter implements ContainerResponseFilter {
    private static final String REFERENCE_HEADER = "Reference";

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        containerResponseContext.getHeaders().add(REFERENCE_HEADER, ThreadContext.get(RequestUUIDAdapter.REQUEST_UUID));
    }
}
