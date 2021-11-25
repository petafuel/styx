package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SinglePaymentInitiationResourceTest extends StyxRESTTest {

    private String currentDate;

    @Override
    protected Application configure() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // your date (java.util.Date)
        cal.add(Calendar.DATE, 1); // You can -/+ x months
        currentDate = simpleDateFormat.format(cal.getTime());

        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(PaymentInitiationResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiateSinglePayment_Consors() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiateSinglePayment_Targo() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSD2TEST4");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiateSinglePayment_ING() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSDDE-BAFIN-125314");
        invocationBuilder.header("PSU-BIC", "INGDDEFF");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE25500105175415042884\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"0.01\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"***REMOVED***\"},\"creditorName\":\"***REMOVED***\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testHrefSerializer() {
        String body = "{ \"transactionStatus\": \"RCVD\", \"paymentId\": \"37e92610-4e99-4c84-a15a-0c500be57440\", \"_links\":{ \"scaRedirect\":\"https://myaccount.ing.com/payment-initiation/37e92610-4e99-4c84-a15a-0c500be57440/XX\", \"self\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440\", \"status\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440/status\", \"delete\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440\" } }";
        String bodyWithHref = "{\"transactionStatus\":\"RCVD\",\"paymentId\":\"I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\",\"_links\":{\"scaRedirect\":{\"href\":\"https://idp-mock.sparda-n.de.schulung.sparda.de/oauth2/authorize?bic=TEST7999&client_id=PSDDE-BAFIN-125314&redirect_uri=https://preprod-styx.paycenter.de:8450/v1/callbacks/payment/ok/9576ac9d-8915-40e1-b8a9-bfd22aadbf47&response_type=code&scope=PIS:tx-6d1df3fec71a02567bab0876cf65616e88c9467f0af55a3c5175e5d89d032753&code_challenge_method=S256&code_challenge=lRPHsFD6rWW1zJlodkYWMRdV0K9uY29EXe_L7ZM_SZc\"},\"self\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\"},\"status\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/status\"},\"scaStatus\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/authorisations/fb49667c-3ce3-4170-b62b-17fbcac3c055\"}},\"scaStatus\":\"received\"}";

        Jsonb jsonb = JsonbBuilder.create();
        InitiatedPayment payment1 = jsonb.fromJson(body, InitiatedPayment.class);
        InitiatedPayment payment2 = jsonb.fromJson(bodyWithHref, InitiatedPayment.class);
        Assert.assertNotNull(payment1.getPaymentId());
        Assert.assertNotNull(payment2.getPaymentId());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiateBulkPayment_Targo() {
        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"requestedExecutionDate\":\"" + currentDate + "\",\"payments\":[{\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", BulkPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiatePeriodicPayment_Consors() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiatePeriodicPayment_Targo() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSD2TEST4");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    @DisplayName(
            "initiateSinglePayment_Fiducia -> Test json to XML conversion of payment initation request within styx to aspsp interface")
    public void initiateSinglePayment_Fiducia() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiateBulkPayment_Fiducia() {
        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"payments\":[{\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"32.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"Gimmeyourmoney\",\"remittanceInformationUnstructured\":\"Myothervwz\"}]}", BulkPaymentInitiation.class);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void initiatePeriodicPayment_Fiducia() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }
}
