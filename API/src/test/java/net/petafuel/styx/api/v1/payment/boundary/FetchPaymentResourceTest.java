package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.BulkPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FetchPaymentResourceTest extends StyxRESTTest {
    private String currentDate;

    @Override
    protected Application configure() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // your date (java.util.Date)
        cal.add(Calendar.DATE, 1); // You can -/+ x months
        currentDate = simpleDateFormat.format(cal.getTime());
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config.register(PaymentInitiationResource.class).register(FetchPaymentResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchSinglePayment() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(201, response.getStatus());
        PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);

        Invocation getPayment = target("/v1/payments/sepa-credit-transfers/" + paymentResponse.getPaymentId()).request()
                .header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08")
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", "CSDBDE71")
                .header("PSU-IP-Address", "192.168.8.78")
                .header("redirectPreferred", true)
                .buildGet();
        response = getPayment.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchPeriodicPayment() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-Successful");
        invocationBuilder.header("PSU-BIC", "CSDBDE71");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", PeriodicPaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(201, response.getStatus());
        PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);

        Invocation getPayment = target("/v1/periodic-payments/sepa-credit-transfers/" + paymentResponse.getPaymentId()).request()
                .header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08")
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", "CSDBDE71")
                .header("PSU-IP-Address", "192.168.8.78")
                .header("redirectPreferred", true)
                .buildGet();
        response = getPayment.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchBulkPayment() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"payments\":[{\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"32.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"Gimmeyourmoney\",\"remittanceInformationUnstructured\":\"Myothervwz\"}]}", BulkPaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(201, response.getStatus());
        PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);

        Invocation getPayment = target("/v1/bulk-payments/sepa-credit-transfers/" + paymentResponse.getPaymentId()).request()
                .header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08")
                .header("PSU-ID", "PSU-1234")
                .header("PSU-BIC", "GENODEF1M03")
                .header("PSU-IP-Address", "192.168.8.78")
                .header("redirectPreferred", true)
                .buildGet();
        response = getPayment.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void fetchPaymentInvalidPaymentService() throws IOException {
        Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSU-1234");
        invocationBuilder.header("PSU-BIC", "GENODEF1M03");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"}, \"payments\":[{\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"},{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE87200500001234567890\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"32.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE23100120020123456789\"},\"creditorName\":\"Gimmeyourmoney\",\"remittanceInformationUnstructured\":\"Myothervwz\"}]}", BulkPaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(201, response.getStatus());
        PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);

        Invocation getPayment = target("/v1/periodic-payments/sepa-credit-transfers/" + paymentResponse.getPaymentId()).request()
                .header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08")
                .header("PSU-ID", "PSU-1234")
                .header("PSU-BIC", "GENODEF1M03")
                .header("PSU-IP-Address", "192.168.8.78")
                .header("redirectPreferred", true)
                .buildGet();
        response = getPayment.invoke(Response.class);
        Assertions.assertEquals(500, response.getStatus());
    }
}
