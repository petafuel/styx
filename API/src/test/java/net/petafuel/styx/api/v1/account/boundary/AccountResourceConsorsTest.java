package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.api.v1.consent.boundary.ConsentResourcesConsorsTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.TextListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountResourceConsorsTest extends StyxRESTTest {
    private static final String BIC = "CSDBDE71";
    static String consentId;
    //@TODO replace this with an account id selected from the account list call
    static String accountId = "9b86539d-589b-4082-90c2-d725c019777f";

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(AccountResource.class);

        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        Result result = junit.run(
                ConsentResourcesConsorsTest.class);

        consentId = ConsentResourcesConsorsTest.consentId;

        if (consentId == null) {
            Assertions.fail("consentId could not be retrieved from previous ConsentResourcesConsorsTest execution");
        }

        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountDetails() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId).request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "127.0.0.1");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("consentId", consentId);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        AccountDetailResponse.AccountJson accountDetails = response.readEntity(AccountDetailResponse.class).getAccount();

        Assertions.assertEquals(accountId, accountDetails.getResourceId());
        Assertions.assertNotNull(accountDetails.getIban());
    }
}
