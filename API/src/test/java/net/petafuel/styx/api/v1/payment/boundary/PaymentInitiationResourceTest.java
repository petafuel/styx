package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentInitiationResourceTest extends StyxRESTTest {

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(PaymentInitiationResource.class);
    }

    @Test
    @Tag("integration")
    public void initiateSinglePayment_Consors() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2022-02-10\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiateSinglePayment_Targo() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSD2TEST4");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2020-02-10\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiateBulkPayment_Targo() {
        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"requestedExecutionDate\":\"2020-02-10\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2020-02-10\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2020-02-10\"}]}", BulkPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiatePeriodicPayment_Consors() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"2020-02-20\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2020-02-10\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiatePeriodicPayment_Targo() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"2020-02-20\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"2020-02-10\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSD2TEST4");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    @DisplayName(
            "initiateSinglePayment_Fiducia -> Test json to XML conversion of payment initation request within styx to aspsp interface")
    public void initiateSinglePayment_Fiducia() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
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
    @Tag("integration")
    public void initiateBulkPayment_Fiducia() {
        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"32.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"Gimmeyourmoney\",\"remittanceInformationUnstructured\":\"Myothervwz\"}]}", BulkPaymentInitiation.class);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiatePeriodicPayment_Fiducia() {
        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"2020-02-20\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"}]}", PeriodicPaymentInitiation.class);

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assert.assertEquals(201, response.getStatus());
    }
}
