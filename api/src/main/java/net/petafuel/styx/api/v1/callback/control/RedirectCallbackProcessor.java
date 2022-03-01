package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Process callback data from received callbacks
 */
public class RedirectCallbackProcessor {
    private static final Logger LOG = LogManager.getLogger(RedirectCallbackProcessor.class);
    private RedirectCallbackProcessor() {
    }

    /**
     * Decides whether the received callback will result in a successful redirect or an error redirect
     *
     * @param serviceRealm   should be payment or consent, anything else should be unknown
     * @param realmParameter should one of RealmParameter
     * @param identifier     should be a resource if as in a xrequestid linked to a consent or payment
     * @return whether styx will redirect to /status/success or /status/error
     */
    public static RedirectStatus processCallback(ServiceRealm serviceRealm, RealmParameter realmParameter, String identifier) {
        if (ServiceRealm.UNKNOWN.equals(serviceRealm)) {
            LOG.warn("Unknown Realm in callback for serviceRealm={}, realmParameter={}, identifier={}", serviceRealm, realmParameter, identifier);
            //if there is no information about the realm but the callbackStatus is ok, return success with warning
            //identifier might also be null but does not matter at this point
            if (RealmParameter.OK.equals(realmParameter)) {
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        } else if (ServiceRealm.PAYMENT.equals(serviceRealm)) {
            //handle callback received due to a payment initiation SCA
            if (RealmParameter.OK.equals(realmParameter)) {
                LOG.info("Redirect Callback on serviceRealm={}, identifier={} received successful SCA completion realmParameter={}", serviceRealm, identifier, realmParameter);
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                LOG.warn("Redirect Callback on serviceRealm={}, identifier={} received failed SCA completion realmParameter={}", serviceRealm, identifier, realmParameter);
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        } else {
            //handle callback received due to a consent SCA
            if (RealmParameter.OK.equals(realmParameter)) {
                LOG.info("Redirect Callback on serviceRealm={}, identifier={} received successful SCA completion realmParameter={}", serviceRealm, identifier, realmParameter);
                return new RedirectStatus(StatusType.SUCCESS, identifier);
            } else {
                LOG.warn("Redirect Callback on serviceRealm={}, identifier={} received failed SCA completion realmParameter={}", serviceRealm, identifier, realmParameter);
                return new RedirectStatus(StatusType.ERROR, identifier);
            }
        }


    }

}
