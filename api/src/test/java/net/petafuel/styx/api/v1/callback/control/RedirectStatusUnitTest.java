package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

class RedirectStatusUnitTest {
    @ParameterizedTest
    @ArgumentsSource(CallbackTestDataProvider.class)
    void testPaymentResourceRealm_SCACallback_Ok(RedirectCallbackProcessor.REALM realm, String status, String identifer, StatusType expected) {
        RedirectStatus redirectStatus = RedirectCallbackProcessor.processCallback(realm, status, identifer);
        Assertions.assertEquals(expected, redirectStatus.getStatusType());
    }

    static class CallbackTestDataProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(RedirectCallbackProcessor.REALM.PAYMENT, "ok", "provided", StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.PAYMENT, "ok", null, StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.PAYMENT, "nok", "provided", StatusType.ERROR),
                    Arguments.of(RedirectCallbackProcessor.REALM.PAYMENT, "nok", null, StatusType.ERROR),

                    Arguments.of(RedirectCallbackProcessor.REALM.CONSENT, "ok", "provided", StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.CONSENT, "ok", null, StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.CONSENT, "nok", "provided", StatusType.ERROR),
                    Arguments.of(RedirectCallbackProcessor.REALM.CONSENT, "nok", null, StatusType.ERROR),

                    Arguments.of(RedirectCallbackProcessor.REALM.UNKNOWN, "ok", "provided", StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.UNKNOWN, "ok", null, StatusType.SUCCESS),
                    Arguments.of(RedirectCallbackProcessor.REALM.UNKNOWN, "nok", "provided", StatusType.ERROR),
                    Arguments.of(RedirectCallbackProcessor.REALM.UNKNOWN, "nok", null, StatusType.ERROR)
            );
        }
    }
}
