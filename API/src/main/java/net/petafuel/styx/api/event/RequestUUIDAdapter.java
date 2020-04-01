package net.petafuel.styx.api.event;

import org.apache.logging.log4j.ThreadContext;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.UUID;

public class RequestUUIDAdapter implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent arg0) {
        ThreadContext.put("requestUUID", UUID.randomUUID().toString());
    }

    public void requestDestroyed(ServletRequestEvent arg0) {
        ThreadContext.clearMap();
    }
}
