package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.control.StatusHelper;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.RedirectStep;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.AuthorizationCodeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;

public class CallbackHandler {
    private static final Logger LOG = LogManager.getLogger(CallbackHandler.class);

    public Response handleCallback(String realm, String param, String xRequestId, OAuthCallback oAuthCallback) {
        RedirectCallbackProcessor.REALM requestedRealm;

        try {
            requestedRealm = RedirectCallbackProcessor.REALM.valueOf(realm.toUpperCase());
        } catch (IllegalArgumentException unknownRealmException) {
            LOG.warn("Callback was received with an unknown resource realm={}", realm);
            requestedRealm = RedirectCallbackProcessor.REALM.UNKNOWN;
        }
        if (oAuthCallback != null && oAuthCallback.getCode() != null) {
            String path = String.format("%s/%s/%s", realm, param, xRequestId);
            return  handleOAuth2(oAuthCallback, path);
        }
        RedirectStatus redirectStatus = RedirectCallbackProcessor.processCallback(requestedRealm, param, xRequestId);

        return StatusHelper.createStatusRedirection(redirectStatus);
    }

    public Response handleOAuth2(OAuthCallback oAuthCallback, String path) {
        if (oAuthCallback.getError() == null && handleSuccessfulOAuth2(oAuthCallback.getCode(), oAuthCallback.getState(), OAuthService.SCA, path)) {
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.SUCCESS,  oAuthCallback.getState());
            return StatusHelper.createStatusRedirection(redirectStatus);
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}",  oAuthCallback.getError(),  oAuthCallback.getErrorDescription(),  oAuthCallback.getState());
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.ERROR,  oAuthCallback.getState());
            return StatusHelper.createStatusRedirection(redirectStatus);
        }
    }

    public Response handlePreStepOAuth2(String code, String state, String error, String errorMessage, String path) {
        OAuthSession oAuthSession = PersistentOAuthSession.getByState(state);
        if (error == null && handleSuccessfulOAuth2(code, state, OAuthService.PREAUTH, path)) {
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.SUCCESS, oAuthSession.getState(), RedirectStep.PREAUTH);
            return StatusHelper.createStatusRedirection(redirectStatus);
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", error, errorMessage, state);
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.ERROR, oAuthSession.getState(), RedirectStep.PREAUTH);
            return StatusHelper.createStatusRedirection(redirectStatus);
        }
    }

    private boolean handleSuccessfulOAuth2(String code, String state, String oauthType, String path) {
        OAuthService service = new OAuthService();
        try {
            OAuthSession stored = PersistentOAuthSession.getByState(state);
            AuthorizationCodeRequest request = new AuthorizationCodeRequest(code, stored.getCodeVerifier());
            request.setRedirectUri(request.getRedirectUri() + path);
            if (stored.getScope().contains("tx") || oauthType.equals(OAuthService.PREAUTH)) {
                request.setJsonBody(false);
            }

            OAuthSession authorized = service.tokenRequest(stored.getTokenEndpoint(), request);
            authorized.setState(state);
            PersistentOAuthSession.update(authorized);
            LOG.info("Successfully received callback from ASPSP for OAuthSession state={}", stored.getState());
            return true;
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
    }
}
