package net.petafuel.styx.api.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class AccessTraceAdapter implements ServletRequestListener {
    private static final Logger LOG = LogManager.getLogger(AccessTraceAdapter.class);

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        Response response = ((Request) servletRequestEvent.getServletRequest()).getResponse();
        LOG.info("Styx REST Interface outgoing response status={}", response.getStatus());
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        Request request = ((Request) servletRequestEvent.getServletRequest());
        LOG.info("Styx REST Interface incoming request uri={}, method={}", request.getRequestURI(), request.getMethod());
    }
}
