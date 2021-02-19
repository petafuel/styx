package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Process callback data from received callbacks
 */
public class RedirectCallbackProcessor {
    public static final String TPP_SUCCESS_REDIRECT_PARAM = "ok";
    private static final Logger LOG = LogManager.getLogger(RedirectCallbackProcessor.class);

    private RedirectCallbackProcessor() {
    }

    /**
     * Decides whether the received callback will result in a successful redirect or an error redirect
     *
     * @param realm          should be payment or consent, anything else should be unknown
     * @param callbackStatus should be ok or nok
     * @param identifier     should be a resource if as in a xrequestid linked to a consent or payment
     * @return whether styx will redirect to /status/success or /status/error
     */
    public static RedirectStatus processCallback(REALM realm, String callbackStatus, String identifier) {
        if (REALM.UNKNOWN.equals(realm)) {
            LOG.warn("Unknown Realm in callback for realm={}, callbackStatus={}, identifier={}", realm, callbackStatus, identifier);
            //if there is no information about the realm but the callbackStatus is ok, return success with warning
            //identifier might also be null but does not matter at this point
            if (TPP_SUCCESS_REDIRECT_PARAM.equalsIgnoreCase(callbackStatus)) {
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        } else if (REALM.PAYMENT.equals(realm)) {
            //handle callback received due to a payment initiation SCA
            if (TPP_SUCCESS_REDIRECT_PARAM.equalsIgnoreCase(callbackStatus)) {
                LOG.info("Redirect Callback on realm={}, identifier={} received successful SCA completion callbackStatus={}", realm, identifier, callbackStatus);
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                LOG.warn("Redirect Callback on realm={}, identifier={} received failed SCA completion callbackStatus={}", realm, identifier, callbackStatus);
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        } else {
            //handle callback received due to a consent SCA
            if (TPP_SUCCESS_REDIRECT_PARAM.equalsIgnoreCase(callbackStatus)) {
                LOG.info("Redirect Callback on realm={}, identifier={} received successful SCA completion callbackStatus={}", realm, identifier, callbackStatus);
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                LOG.warn("Redirect Callback on realm={}, identifier={} received failed SCA completion callbackStatus={}", realm, identifier, callbackStatus);
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        }


    }

    public enum REALM {
        PAYMENT,
        CONSENT,
        UNKNOWN,
        OAUTH
    }
}
