package net.petafuel.styx.api.v1.callback.boundary;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class CallbackResourceTest {

    private CallbackResource cut = new CallbackResource();

    @Test
    @Tag("integration")
    public void testCallback() {
        Response r1 = cut.processCallback("49a52a96-b5ad-41e5-91e5-ff3c77e483dd");
        Assert.assertEquals(200, r1.getStatus());
    }
}
