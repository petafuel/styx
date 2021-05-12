package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import net.petafuel.styx.api.v1.status.entity.StatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.stream.Stream;

class CallbackHandlerUnitTest {
    @ParameterizedTest
    @ArgumentsSource(CallbackHandlerUnitTest.CallbackDataProvider.class)
    void testPaymentResourceRealm_SCACallback_Ok(String serviceRealm, String realmParameter, String identifer, OAuthCallback oAuthCallback, StatusType expected) {
        Response response = CallbackHandler.handleCallback(serviceRealm, realmParameter, identifer, oAuthCallback);
        Assertions.assertEquals(307, response.getStatus());
        Assertions.assertTrue(response.getLocation().getRawPath().contains(expected.name().toLowerCase()));
    }

    static class CallbackDataProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            String xrid = UUID.randomUUID().toString();
            OAuthCallback invalidXrequestId = new OAuthCallback();
            invalidXrequestId.setError("error_code");
            invalidXrequestId.setState(xrid);

            return Stream.of(
                    Arguments.of("payment", "ok", xrid, invalidXrequestId, StatusType.ERROR),
                    Arguments.of("payment", "failed", UUID.randomUUID().toString(), new OAuthCallback(), StatusType.ERROR),
                    Arguments.of("something", "ok", UUID.randomUUID().toString(), new OAuthCallback(), StatusType.SUCCESS),
                    Arguments.of("payment", "ok", UUID.randomUUID().toString(), new OAuthCallback(), StatusType.SUCCESS)
            );
        }
    }
}