package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.control.StatusHelper;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;

public class CallbackHandler {
    private static final Logger LOG = LogManager.getLogger(CallbackHandler.class);

    private CallbackHandler() {
    }

    public static Response handleCallback(String realm, String param, String xRequestId, OAuthCallback oAuthCallback) {
        RedirectCallbackProcessor.REALM requestedRealm;
        RedirectStatus redirectStatus = null;
        try {
            requestedRealm = RedirectCallbackProcessor.REALM.valueOf(realm.toUpperCase());
        } catch (IllegalArgumentException unknownRealmException) {
            LOG.warn("Callback was received with an unknown resource realm={}", realm);
            requestedRealm = RedirectCallbackProcessor.REALM.UNKNOWN;
        }
        LOG.info("Received callback for resource realm={}, param={}, originRequestUUID={}, oAuthCallback={}", requestedRealm, param, xRequestId, oAuthCallback);
        //If we receive a callback that seems to be from a previous oauth sca(containing a code query parameter from the aspsp)
        //We also check for the state which we need to get the token, if the sate query parameter is not present we try to do
        //a normal redirect callback as the oauth approach is always going to fail without state
        if (oAuthCallback != null && (oAuthCallback.getCode() != null || oAuthCallback.getError() != null)) {
            if (oAuthCallback.getState() != null) {
                redirectStatus = OAuthCallbackProcessor.processCallback(requestedRealm, param, xRequestId, oAuthCallback);
            } else {
                LOG.warn("Received callback seems to be oauth(code query param present) but state is missing. Continue as redirect");
            }
        }
        if (redirectStatus == null) {
            redirectStatus = RedirectCallbackProcessor.processCallback(requestedRealm, param, xRequestId);
        }

        return StatusHelper.createStatusRedirection(redirectStatus);
    }
}
