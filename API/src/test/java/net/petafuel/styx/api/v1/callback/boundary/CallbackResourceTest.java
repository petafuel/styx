package net.petafuel.styx.api.v1.callback.boundary;

import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

public class CallbackResourceTest {

    private CallbackResource cut = new CallbackResource();

    @Test
    @Tag("integration")
    public void testCallback() {
        MultivaluedHashMap<String, String> m1 = new MultivaluedHashMap<String, String>();
        m1.add("X-Request-Id", "49a52a96-b5ad-41e5-91e5-ff3c77e483dd");
        ResteasyHttpHeaders headers = new ResteasyHttpHeaders(m1);
        Response r1 = cut.processCallback(
                headers,
                "49a52a96-b5ad-41e5-91e5-ff3c77e483dd",
                null,
                null,
                null,
                null,
                "{\"x-request-id\":\"49a52a96-b5ad-41e5-91e5-ff3c77e483dd\"}"
        );
        Assert.assertEquals(200, r1.getStatus());
    }
}
