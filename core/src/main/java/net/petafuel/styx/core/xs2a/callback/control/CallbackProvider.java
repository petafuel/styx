package net.petafuel.styx.core.xs2a.callback.control;

import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.utils.CoreProperties;

public class CallbackProvider {
    /**
     * styx.base.url/serivce-realm/realm-parameter/origin-x-request-id
     * styx.base.url -> should contain the full url with the /callbacks/ route at the end and a trailing slash
     */
    private static final String DEFAULT_CALLBACK_URI_SCHEMA = "%s%s/%s/%s";

    private CallbackProvider() {
    }

    public static String generateCallbackUrl(ServiceRealm serviceRealm, RealmParameter realmParameter, String originXRequestId) {
        return fillUrl(serviceRealm.name().toLowerCase(), realmParameter.name().toLowerCase(), originXRequestId);
    }

    private static String fillUrl(String serviceRealm, String realmParameter, String originXRequestId) {
        return String.format(DEFAULT_CALLBACK_URI_SCHEMA, System.getProperty(CoreProperties.STYX_REDIRECT_URL), serviceRealm, realmParameter, originXRequestId);
    }
}
