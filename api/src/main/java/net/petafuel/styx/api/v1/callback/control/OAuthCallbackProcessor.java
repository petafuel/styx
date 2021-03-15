package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.control.StatusHelper;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.RedirectStep;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.persistence.models.PaymentEntry;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.AuthorizationCodeRequest;
import net.petafuel.styx.keepalive.tasks.PaymentStatusPoll;
import net.petafuel.styx.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static net.petafuel.styx.api.v1.callback.control.RedirectCallbackProcessor.TPP_SUCCESS_REDIRECT_PARAM;

public class OAuthCallbackProcessor {
    private static final Logger LOG = LogManager.getLogger(OAuthCallbackProcessor.class);

    private OAuthCallbackProcessor() {
    }

    public static RedirectStatus processCallback(RedirectCallbackProcessor.REALM realm, String param, String identifier, OAuthCallback oAuthCallback) {
        if (RedirectCallbackProcessor.REALM.UNKNOWN.equals(realm)) {
            return handleUnknownRealm(realm, param, identifier);
        }
        //OAUTH Realm allowed for legacy reasons, should be deprecated in the future
        if (RedirectCallbackProcessor.REALM.PAYMENT.equals(realm) || RedirectCallbackProcessor.REALM.OAUTH.equals(realm)) {
            return handlePaymentRealm(realm, param, identifier, oAuthCallback);
        } else {
            return handleConsentRealm(realm, param, identifier, oAuthCallback);
        }
    }

    private static RedirectStatus handleUnknownRealm(RedirectCallbackProcessor.REALM realm, String param, String identifier) {
        LOG.warn("Unknown Realm in callback for realm={}, callbackStatus={}, identifier={}", realm, param, identifier);
        //if there is no information about the realm but the callbackStatus is ok, return success with warning
        //identifier might also be null but does not matter at this point
        if (TPP_SUCCESS_REDIRECT_PARAM.equalsIgnoreCase(param)) {
            return new RedirectStatus(StatusType.SUCCESS, identifier);
        } else {
            return new RedirectStatus(StatusType.ERROR, identifier);
        }
    }

    private static RedirectStatus handlePaymentRealm(RedirectCallbackProcessor.REALM realm, String param, String identifier, OAuthCallback oAuthCallback) {
        String path = String.format("%s/%s/%s", realm.name().toLowerCase(), param, identifier);
        if (oAuthCallback.getError() == null && handleSuccessfulOAuth2(oAuthCallback.getCode(), oAuthCallback.getState(), OAuthService.SCA, path)) {
            PaymentEntry paymentEntry = PersistentPayment.getById(identifier);
            XS2AStandard xs2AStandard;
            try {
                xs2AStandard = (new SAD()).getBankByBIC(paymentEntry.getBic());
            } catch (BankNotFoundException | BankLookupFailedException e) {
                LOG.error("OAuth Callback on realm={}, identifier={}, param={} failed due to SAD not being able to initialize the aspsp connected to the payment SCA", realm, identifier, param, e);
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
            // In case of oauth we will not schedule the task during payment initiation but here after we received a callback
            XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
            xs2AFactoryInput.setPaymentId(paymentEntry.getPaymentId());
            xs2AFactoryInput.setPaymentService(paymentEntry.getPaymentService());
            xs2AFactoryInput.setPaymentProduct(paymentEntry.getPaymentProduct());
            ThreadManager.getInstance().queueTask(new PaymentStatusPoll(xs2AFactoryInput, xs2AStandard.getAspsp().getBic(), UUID.fromString(paymentEntry.getId())));
            return new RedirectStatus(StatusType.SUCCESS, oAuthCallback.getState());

        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", oAuthCallback.getError(), oAuthCallback.getErrorDescription(), oAuthCallback.getState());
            return new RedirectStatus(StatusType.ERROR, oAuthCallback.getState());
        }
    }

    private static RedirectStatus handleConsentRealm(RedirectCallbackProcessor.REALM realm, String param, String identifier, OAuthCallback oAuthCallback) {
        String path = String.format("%s/%s/%s", realm.name().toLowerCase(), param, identifier);
        if (oAuthCallback.getError() == null && handleSuccessfulOAuth2(oAuthCallback.getCode(), oAuthCallback.getState(), OAuthService.SCA, path)) {
            LOG.info("OAuth Callback on realm={}, identifier={} received successful SCA completion callbackStatus={}", realm, identifier, param);
            return new RedirectStatus(StatusType.SUCCESS, oAuthCallback.getState());
        } else {
            LOG.error("failed oauth2 callback error={}, errorMessage={}, state={}", oAuthCallback.getError(), oAuthCallback.getErrorDescription(), oAuthCallback.getState());
            return new RedirectStatus(StatusType.ERROR, oAuthCallback.getState());
        }
    }

    /**
     * Legacy method to handle pre-step authentication for Sparda, might be deprecated in the future
     *
     * @param code         received from the bank
     * @param state        received from the bank - matches in styx database
     * @param error        received from the bank on error
     * @param errorMessage received from the bank on error
     * @param path         to be used as redirect url to styx
     * @return returns a jaxrs response object to be returned to a client
     */
    public static Response handlePreStepOAuth2(String code, String state, String error, String errorMessage, String path) {
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

    /**
     * This will retrieve and save the access_token an other oauth related data from an ASPSP into the styx system
     *
     * @param code      oauth query param
     * @param state     oauth query param
     * @param oauthType allows for preauth for legacy reasons
     * @param path      redirect path
     * @return whether we were able to retrieve the access_token successfully
     */
    private static boolean handleSuccessfulOAuth2(String code, String state, String oauthType, String path) {
        OAuthService service = new OAuthService();
        try {
            OAuthSession stored = PersistentOAuthSession.getByState(state);
            AuthorizationCodeRequest request = new AuthorizationCodeRequest(code, stored.getCodeVerifier());
            request.setRedirectUri(request.getRedirectUri() + path);
            request.setJsonBody(false);
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
