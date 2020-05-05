package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.account.control.TransactionListResponseAdapter;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.api.v1.consent.boundary.ConsentResourcesTargoTest;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.BeforeClass;
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
public class AccountResourceTragoTest extends StyxRESTTest {
    private static final String BIC = "CMCIDEDD";
    static String consentId;
    //@TODO replace this with an account id selected from the account list call
    static String accountId = "6612c7532cf7566e170a5788adc141c601dda17514bc1f498c054013137835e4";

    @BeforeClass
    public static void getConsentId() {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        Result result = junit.run(
                ConsentResourcesTargoTest.class);

        consentId = ConsentResourcesTargoTest.consentId;

        if (consentId == null) {
            Assertions.fail("consentId could not be retrieved from previous ConsentResourcesTargoTest execution");
        }
    }

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(AccountResource.class);

        if (targobankToken == null) {
            Assertions.fail("test.token.targobank is not set in test api.properties");
        }

        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountDetails() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId).request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", targobankToken);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        AccountDetailResponse.AccountJson accountDetails = response.readEntity(AccountDetailResponse.class).getAccount();

        Assertions.assertEquals(accountId, accountDetails.getResourceId());
        Assertions.assertNotNull(accountDetails.getIban());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountBalances() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId + "/balances").request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", targobankToken);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        BalanceContainer accountDetails = response.readEntity(BalanceContainer.class);
        if (accountDetails.getAccount() != null) {
            Assertions.assertNotNull(accountDetails.getAccount().getIban());
        }
        accountDetails.getBalances().forEach(balance -> {
            Assertions.assertNotNull(balance.getBalanceAmount().getAmount());
            Assertions.assertNotNull(balance.getBalanceAmount().getCurrency());
            Assertions.assertNotNull(balance.getBalanceType());
        });
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountTransactions() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId + "/transactions").queryParam("dateFrom", "2019-01-01").queryParam("bookingStatus", "booked").request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", targobankToken);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        TransactionListResponseAdapter accountDetails = response.readEntity(TransactionListResponseAdapter.class);
        if (accountDetails.getTransactions() != null) {
            accountDetails.getTransactions().forEach(transactionAdapted -> {
                Assertions.assertNotNull(transactionAdapted.getBookingStatus());
                Assertions.assertNotNull(transactionAdapted.getTransactionAmount());
                Assertions.assertNotNull(transactionAdapted.getTransactionAmount().getAmount());
                Assertions.assertNotNull(transactionAdapted.getBookingDate());
                Assertions.assertNotNull(transactionAdapted.getValueDate());
                Assertions.assertNotNull(transactionAdapted.getPurpose());
            });
        }
    }
}
