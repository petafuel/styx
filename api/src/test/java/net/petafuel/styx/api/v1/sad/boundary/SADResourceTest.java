package net.petafuel.styx.api.v1.sad.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.json.JsonObject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SADResourceTest extends StyxRESTTest {
    private static final String BIC = "HYVEDEMM488";
    private static final String WRONG_BIC = "1337";

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        config.register(SADResource.class);
        return config;
    }

    @Test
    @Category(IntegrationTest.class)
    public void GetAspspDataSuccessTest() throws BankLookupFailedException, BankNotFoundException {
        Aspsp aspsp = getAspspByBic(BIC);
        Map<String, ImplementerOption> implementerOptions = aspsp.getConfig().getImplementerOptions();

        Invocation.Builder invocationBuilder = target("/v1/aspsp/" + BIC).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("Content-Type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());

        JsonObject jsonResponse = response.readEntity(JsonObject.class);
        JsonObject jsonScaApproaches = (JsonObject) jsonResponse.get("scaApproaches");
        JsonObject jsonSupportedServices = (JsonObject) jsonResponse.get("supportedServices");
        JsonObject jsonSupportedServicesAis = (JsonObject) jsonSupportedServices.get("ais");
        JsonObject jsonSupportedServicesCof = (JsonObject) jsonSupportedServices.get("cof");
        JsonObject jsonSupportedServicesPis = (JsonObject) jsonSupportedServices.get("pis");
        Assertions.assertEquals(aspsp.isActive(), jsonResponse.getBoolean("active"));
        Assertions.assertEquals(aspsp.getName(), jsonResponse.getString("name"));
        Assertions.assertFalse(jsonResponse.getBoolean("multicurrencyAccountsSupported"));
        Assertions.assertFalse(jsonResponse.getBoolean("prestepRequired"));
        Assertions.assertTrue(jsonResponse.containsKey("scaApproaches"));
        Assertions.assertEquals(
                implementerOptions.get("IO5").getOptions().get("decoupled"),
                jsonScaApproaches.getBoolean("decoupled")
        );
        Assertions.assertEquals(
                implementerOptions.get("IO5").getOptions().get("embedded"),
                jsonScaApproaches.getBoolean("embedded")
        );
        Assertions.assertEquals(
                implementerOptions.get("IO5").getOptions().get("oauth"),
                jsonScaApproaches.getBoolean("oAuth")
        );
        Assertions.assertEquals(
                implementerOptions.get("IO5").getOptions().get("redirect"),
                jsonScaApproaches.getBoolean("redirect")
        );
        Assertions.assertTrue(jsonResponse.containsKey("supportedServices"));
        Assertions.assertTrue(jsonSupportedServices.containsKey("ais"));
        Assertions.assertTrue(jsonSupportedServicesAis.getBoolean("accountDetails"));
        Assertions.assertTrue(jsonSupportedServicesAis.getBoolean("accountList"));
        Assertions.assertFalse(jsonSupportedServicesAis.getBoolean("accountsAccountIdTransactionsResourceId"));
        Assertions.assertFalse(jsonSupportedServicesAis.getBoolean("accountsAccountIdTransactionsWithBalance"));
        Assertions.assertFalse(jsonSupportedServicesAis.getBoolean("accountsAccountIdWithBalance"));
        Assertions.assertFalse(jsonSupportedServicesAis.getBoolean("accountsWithBalance"));
        Assertions.assertTrue(jsonSupportedServices.containsKey("cof"));
        Assertions.assertTrue(jsonSupportedServicesCof.containsKey("fundsConfirmation"));
        Assertions.assertTrue(jsonSupportedServices.containsKey("pis"));
        Assertions.assertTrue(jsonSupportedServicesPis.getBoolean("bulkPayments"));
        Assertions.assertTrue(jsonSupportedServicesPis.getBoolean("futureDatedPayments"));
        Assertions.assertTrue(jsonSupportedServicesPis.getBoolean("periodicPayments"));
        Assertions.assertTrue(jsonSupportedServicesPis.getBoolean("singlePayments"));

        Response response2 = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response2.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void GetAspspDataWrongBicTest() {
        Invocation.Builder invocationBuilder = target("/v1/aspsp/" + WRONG_BIC).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("Content-Type", "application/json");

        Invocation invocation = invocationBuilder.buildGet();
        Response response = invocation.invoke(Response.class);

        Assertions.assertEquals(409, response.getStatus());
        ResponseEntity responseEntity = response.readEntity(ResponseEntity.class);
        Assertions.assertEquals("The requested ASPSP was not found within SAD for BIC " + WRONG_BIC, responseEntity.getMessage());
        Assertions.assertEquals(ResponseConstant.SAD_ASPSP_NOT_FOUND, responseEntity.getCode());
        Assertions.assertEquals(ResponseCategory.ERROR, responseEntity.getCategory());
        Assertions.assertEquals(ResponseOrigin.STYX, responseEntity.getOrigin());
    }

    /**
     * Gets the aspsp for the provided bic
     *
     * @return Aspsp | null
     */
    private Aspsp getAspspByBic(String bic) throws BankLookupFailedException, BankNotFoundException {
        SAD sad = new SAD();
        return sad.getBankByBIC(bic).getAspsp();
    }
}
