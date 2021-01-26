package net.petafuel.styx.api.v1.callback.control;

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

    public Response handleRedirect(String realm, String param, String xRequestId) {
        RedirectCallbackProcessor.REALM requestedRealm;

        try {
            requestedRealm = RedirectCallbackProcessor.REALM.valueOf(realm.toUpperCase());
        } catch (IllegalArgumentException unknownRealmException) {
            LOG.warn("Callback was received with an unknown resource realm={}", realm);
            requestedRealm = RedirectCallbackProcessor.REALM.UNKNOWN;
        }
        RedirectStatus redirectStatus = RedirectCallbackProcessor.processCallback(requestedRealm, param, xRequestId);

        return StatusHelper.createStatusRedirection(redirectStatus);
    }

    public Response handleOAuth2(String code, String state, String error, String errorMessage) {
        if (error == null && handleSuccessfulOAuth2(code, state, OAuthService.SCA)) {
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.SUCCESS, state, RedirectStep.PREAUTH);
            return StatusHelper.createStatusRedirection(redirectStatus);
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", error, errorMessage, state);
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.ERROR, state);
            return StatusHelper.createStatusRedirection(redirectStatus);
        }
    }

    public Response handlePreStepOAuth2(String code, String state, String error, String errorMessage) {
        OAuthSession oAuthSession = PersistentOAuthSession.getByState(state);
        if (error == null && handleSuccessfulOAuth2(code, state, OAuthService.PREAUTH)) {
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.SUCCESS, oAuthSession.getState(), RedirectStep.PREAUTH);
            return StatusHelper.createStatusRedirection(redirectStatus);
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", error, errorMessage, state);
            RedirectStatus redirectStatus = new RedirectStatus(StatusType.ERROR, oAuthSession.getState(), RedirectStep.PREAUTH);
            return StatusHelper.createStatusRedirection(redirectStatus);
        }
    }

    private boolean handleSuccessfulOAuth2(String code, String state, String oauthType) {
        OAuthService service = new OAuthService();
        try {
            OAuthSession stored = PersistentOAuthSession.getByState(state);
            AuthorizationCodeRequest request = new AuthorizationCodeRequest(code, stored.getCodeVerifier());
            if (oauthType.equals(OAuthService.PREAUTH)) {
                request.setJsonBody(false);
                request.setRedirectUri(request.getRedirectUri() + "oauth/" + OAuthService.PREAUTH + '/' + state);
            } else {
                request.setRedirectUri(request.getRedirectUri() + "oauth/" + OAuthService.SCA);
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
