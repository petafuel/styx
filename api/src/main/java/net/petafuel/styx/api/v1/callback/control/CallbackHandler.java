package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.control.StatusHelper;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;

public class CallbackHandler {
    private static final Logger LOG = LogManager.getLogger(CallbackHandler.class);

    private CallbackHandler() {
    }

    public static Response handleCallback(String serviceRealm, String realmParameter, String xRequestId, OAuthCallback oAuthCallback) {
        ServiceRealm requestedServiceRealm;
        RealmParameter receivedRealmParameter;
        RedirectStatus redirectStatus = null;
        try {
            requestedServiceRealm = ServiceRealm.valueOf(serviceRealm.toUpperCase());
        } catch (IllegalArgumentException unknownRealmException) {
            LOG.warn("Callback was received with an unknown serviceRealm={}", serviceRealm);
            requestedServiceRealm = ServiceRealm.UNKNOWN;
        }
        try {
            receivedRealmParameter = RealmParameter.valueOf(realmParameter.toUpperCase());
        } catch (IllegalArgumentException unknownRealmException) {
            LOG.warn("Callback was received with an unknown realmParameter={}", realmParameter);
            receivedRealmParameter = RealmParameter.UNKNOWN;
        }

        LOG.info("Received callback for resource serviceRealm={}, realmParameter={}, originRequestUUID={}, oAuthCallback={}", requestedServiceRealm, realmParameter, xRequestId, oAuthCallback);
        if (oAuthCallback != null && (oAuthCallback.getCode() != null || oAuthCallback.getError() != null)) {
            redirectStatus = OAuthCallbackProcessor.processCallback(requestedServiceRealm, receivedRealmParameter, xRequestId, oAuthCallback);
        }
        if (redirectStatus == null) {
            redirectStatus = RedirectCallbackProcessor.processCallback(requestedServiceRealm, receivedRealmParameter, xRequestId);
        }

        return StatusHelper.createStatusRedirection(redirectStatus);
    }
}
