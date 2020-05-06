package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.account.control.TransactionListResponseAdapter;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.AccountListResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import javax.mail.internet.ContentType;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountResourceFiduciaTest extends StyxRESTTest {
    private static final String BIC = "GENODEF1M03";
    //This consent is always valid for the fiducia
    private static final String consentId = "CONSENTVALID";
    static String accountId;

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(AccountResource.class);
        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountList() {
        Invocation.Builder invocationBuilder = target("/v1/accounts").request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        AccountListResponse accountListResponse = response.readEntity(AccountListResponse.class);

        Assertions.assertNotNull(accountListResponse.getAccounts());
        Assertions.assertEquals("DE45499999600000005100", accountListResponse.getAccounts().get(0).getIban());
        accountId = accountListResponse.getAccounts().get(0).getResourceId();
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

        Assertions.assertEquals("DE45499999600000005100", accountDetails.getIban());
        Assertions.assertEquals("EUR", accountDetails.getCurrency());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testAccountBalances() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId + "/balances").request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);

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
