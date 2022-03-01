package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.status.entity.RedirectStatus;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

class RedirectProcessorUnitTest {
    @ParameterizedTest
    @ArgumentsSource(CallbackTestDataProvider.class)
    void testPaymentResourceRealm_SCACallback_Ok(ServiceRealm serviceRealm, RealmParameter realmParameter, String identifer, StatusType expected) {
        RedirectStatus redirectStatus = RedirectCallbackProcessor.processCallback(serviceRealm, realmParameter, identifer);
        Assertions.assertEquals(expected, redirectStatus.getStatusType());
    }

    static class CallbackTestDataProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, "provided", StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.OK, null, StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.FAILED, "provided", StatusType.ERROR),
                    Arguments.of(ServiceRealm.PAYMENT, RealmParameter.FAILED, null, StatusType.ERROR),

                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, "provided", StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.OK, null, StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.FAILED, "provided", StatusType.ERROR),
                    Arguments.of(ServiceRealm.CONSENT, RealmParameter.FAILED, null, StatusType.ERROR),

                    Arguments.of(ServiceRealm.UNKNOWN, RealmParameter.OK, "provided", StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.UNKNOWN, RealmParameter.OK, null, StatusType.SUCCESS),
                    Arguments.of(ServiceRealm.UNKNOWN, RealmParameter.FAILED, "provided", StatusType.ERROR),
                    Arguments.of(ServiceRealm.UNKNOWN, RealmParameter.FAILED, null, StatusType.ERROR)
            );
        }
    }
}
