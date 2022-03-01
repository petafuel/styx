package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.UUID;
import java.util.stream.Stream;


class OAuthCallbackProcessorUnitTest {
    @BeforeEach
    void checkConfig() {
        boolean configPresent = false;
        try {
            //BerlinGroup Signer is initialised as soon as the OauthService is initialised
            // which in the end requries the CertificateManager to load and verify the PSD2 certificate
            //If this failes, this test cannot execute properly
            CertificateManager.getInstance();
            configPresent = true;
        } catch (Throwable ignored) {
        }
        Assumptions.assumeTrue(configPresent);
    }

    @ParameterizedTest
    @ArgumentsSource(OAuthCallbackProcessorUnitTest.CallbackTestDataProvider.class)
    void testPaymentResourceRealm_SCACallback_Ok(ServiceRealm serviceRealm, RealmParameter realmParameter, String identifer, OAuthCallback oAuthCallback, StatusType expected) {

        RedirectStatus redirectStatus = OAuthCallbackProcessor.processCallback(serviceRealm, realmParameter, identifer, oAuthCallback);
        Assertions.assertEquals(expected, redirectStatus.getStatusType());
    }

    static class CallbackTestDataProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            OAuthCallback scaErrorCallback = new OAuthCallback();
            scaErrorCallback.setError("test_error");
            scaErrorCallback.setErrorDescription("test_error description");

            OAuthCallback scaErrorCallbackWithCode = new OAuthCallback();
            scaErrorCallbackWithCode.setCode(UUID.randomUUID().toString());
            scaErrorCallbackWithCode.setError("test_error");
            scaErrorCallbackWithCode.setErrorDescription("test_error description");

            OAuthCallback scaSuccessButUnknownSessionState = new OAuthCallback();
            scaSuccessButUnknownSessionState.setCode(UUID.randomUUID().toString());

            OAuthCallback scaSuccessButUnknownInvalidState = new OAuthCallback();
            scaSuccessButUnknownInvalidState.setCode(UUID.randomUUID().toString());
            scaSuccessButUnknownInvalidState.setState(UUID.randomUUID().toString());

            OAuthCallback scaErrorButValidDateIsReceived = new OAuthCallback();
            scaErrorButValidDateIsReceived.setCode(UUID.randomUUID().toString());
            scaErrorButValidDateIsReceived.setState(UUID.randomUUID().toString());

            return Stream.of(
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", scaErrorCallback, StatusType.ERROR),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", scaErrorCallbackWithCode, StatusType.ERROR),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", scaSuccessButUnknownSessionState, StatusType.ERROR),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", scaSuccessButUnknownInvalidState, StatusType.ERROR),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", scaErrorButValidDateIsReceived, StatusType.ERROR),

                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", scaErrorCallback, StatusType.ERROR),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", scaErrorCallbackWithCode, StatusType.ERROR),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", scaSuccessButUnknownSessionState, StatusType.ERROR),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", scaSuccessButUnknownInvalidState, StatusType.ERROR),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", scaErrorButValidDateIsReceived, StatusType.ERROR)
            );
        }
    }
}