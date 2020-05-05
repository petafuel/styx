package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.v1.account.control.TransactionListResponseAdapter;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.api.v1.authentication.boundary.AuthenticationResource;
import net.petafuel.styx.api.v1.consent.boundary.ConsentResourcesConsorsTest;
import net.petafuel.styx.api.v1.payment.boundary.PaymentInitiationResource;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.TextListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.MethodSorters;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountResourceConsorsTest extends StyxRESTTest {
    private static final String BIC = "CSDBDE71";
    static String consentId;
    //@TODO replace this with an account id selected from the account list call
    static String accountId = "9b86539d-589b-4082-90c2-d725c019777f";

    @BeforeClass
    public static void getConsentId() {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        Result result = junit.run(
                ConsentResourcesConsorsTest.class);

        consentId = ConsentResourcesConsorsTest.consentId;

        if (consentId == null) {
            Assertions.fail("consentId could not be retrieved from previous ConsentResourcesConsorsTest execution");
        }
    }

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(AccountResource.class)
                .register(AuthenticationResource.class)
                .register(PaymentInitiationResource.class);

        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void B_testAccountDetails() {
        Invocation.Builder invocationBuilder = target("/v1/accounts/" + accountId).request();
        invocationBuilder.header("token", aisAccessToken);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("consentId", consentId);

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        AccountDetailResponse.AccountJson accountDetails = response.readEntity(AccountDetailResponse.class).getAccount();

        Assertions.assertEquals(accountId, accountDetails.getResourceId());
        Assertions.assertNotNull(accountDetails.getIban());
    }

    @Test
    @Category(IntegrationTest.class)
    public void C_testAccountBalances() {
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
            Assertions.assertNotNull(balance.getBalanceType());
        });
    }

    @Test
    @Category(IntegrationTest.class)
    public void useAccessTokenWithInvalidServiceBinding() {
        Invocation.Builder invocationBuilder = target("/v1/auth").request();
        invocationBuilder.header("token", masterToken);
        invocationBuilder.header("service", "ais");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        JsonObject tokenObject = response.readEntity(JsonObject.class);

        invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", tokenObject.getString("token"));

        invocation = invocationBuilder.buildPost(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
        response = invocation.invoke(Response.class);
        Assertions.assertEquals(403, response.getStatus());
        JsonObject errorObject = response.readEntity(JsonObject.class);

        Assertions.assertEquals(ResponseCategory.ERROR.name(), errorObject.getString("category"));
        Assertions.assertEquals(ResponseConstant.STYX_TOKEN_ACCESS_VIOLATION.name(), errorObject.getString("code"));
        Assertions.assertEquals(ResponseOrigin.CLIENT.name(), errorObject.getString("origin"));
    }

    @Test
    @Category(IntegrationTest.class)
    public void D_testAccountTransactions() {
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
