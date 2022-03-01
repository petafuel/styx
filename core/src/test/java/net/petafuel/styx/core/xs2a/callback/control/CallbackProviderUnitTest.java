package net.petafuel.styx.core.xs2a.callback.control;

import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.utils.CoreProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CallbackProviderUnitTest {
    @Test
    void testUrlGeneration() {
        String testOriginXRequestId = UUID.randomUUID().toString();
        final String url_payment_oauth_ok = System.getProperty(CoreProperties.STYX_REDIRECT_URL) + "payment" + "/ok" + "/" + testOriginXRequestId;
        Assertions.assertEquals(url_payment_oauth_ok, CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.OK, testOriginXRequestId));

        final String url_payment_redirect_failed = System.getProperty(CoreProperties.STYX_REDIRECT_URL) + "payment" + "/failed" + "/" + testOriginXRequestId;
        Assertions.assertEquals(url_payment_redirect_failed, CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.FAILED, testOriginXRequestId));

        final String url_payment_none_ok = System.getProperty(CoreProperties.STYX_REDIRECT_URL) + "payment" + "/failed" + "/" + testOriginXRequestId;
        Assertions.assertEquals(url_payment_none_ok, CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.FAILED, testOriginXRequestId));

        final String url_consent_oauth_ok = System.getProperty(CoreProperties.STYX_REDIRECT_URL) + "consent" + "/ok" + "/" + testOriginXRequestId;
        Assertions.assertEquals(url_consent_oauth_ok, CallbackProvider.generateCallbackUrl(ServiceRealm.CONSENT, RealmParameter.OK, testOriginXRequestId));

        final String url_unknown_oauth_failed = System.getProperty(CoreProperties.STYX_REDIRECT_URL) + "unknown" + "/failed" + "/" + testOriginXRequestId;
        Assertions.assertEquals(url_unknown_oauth_failed, CallbackProvider.generateCallbackUrl(ServiceRealm.UNKNOWN, RealmParameter.FAILED, testOriginXRequestId));
    }
}