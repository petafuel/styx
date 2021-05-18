package net.petafuel.styx.api.v1.callback.control;

import net.petafuel.styx.api.v1.callback.entity.OAuthCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

class CallbackHandlerUnitTest {

    @Test
    void testHandleRedirectCallback_RealmPayment_ParamOk() {
        String xrid = UUID.randomUUID().toString();
        Response response = CallbackHandler.handleCallback(RedirectCallbackProcessor.REALM.PAYMENT.name(), "ok", xrid, new OAuthCallback());
        Assertions.assertEquals(307, response.getStatus());
        Assertions.assertTrue(response.getLocation().getRawPath().contains(xrid));
        Assertions.assertTrue(response.getLocation().getRawPath().contains("success"));
    }

    @Test
    void testHandleRedirectCallback_RealmUnknown_ParamOk() {
        String xrid = UUID.randomUUID().toString();
        Response response = CallbackHandler.handleCallback("something", "ok", xrid, new OAuthCallback());
        Assertions.assertEquals(307, response.getStatus());
        Assertions.assertTrue(response.getLocation().getRawPath().contains(xrid));
        Assertions.assertTrue(response.getLocation().getRawPath().contains("success"));
    }

    @Test
    void testHandleRedirectCallback_RealmPayment_ParamNok() {
        String xrid = UUID.randomUUID().toString();
        Response response = CallbackHandler.handleCallback(RedirectCallbackProcessor.REALM.PAYMENT.name(), "nok", xrid, new OAuthCallback());
        Assertions.assertEquals(307, response.getStatus());
        Assertions.assertTrue(response.getLocation().getRawPath().contains(xrid));
        Assertions.assertTrue(response.getLocation().getRawPath().contains("error"));
    }

    @Test
    void testHandleOauthCallbackError() {
        String xrid = UUID.randomUUID().toString();
        OAuthCallback oAuthCallback = new OAuthCallback();
        oAuthCallback.setError("error_code");
        oAuthCallback.setState(xrid);
        Response response = CallbackHandler.handleCallback(RedirectCallbackProcessor.REALM.PAYMENT.name(), "ok", xrid, oAuthCallback);
        Assertions.assertEquals(307, response.getStatus());
        Assertions.assertTrue(response.getLocation().getRawPath().contains("error"));
    }
}