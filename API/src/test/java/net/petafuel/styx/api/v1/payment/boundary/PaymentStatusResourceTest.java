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
import org.junit.jupiter.api.TestInstance;

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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentStatusResourceTest extends StyxRESTTest {

    public static final String CONSORS_BIC = "CSDBDE71";
    public static final String SPARKASSE_BIC = "BYLADEM1FSI";

    public static final String ACCESS_TOKEN = "d0b10916-7926-4b6c-a90c-3643c62e4b08";

    private String consorsSinglePaymentId;
    private String consorsPeriodicPaymentId;

    private String sparkasseSinglePaymentId;
    private String sparkasseBulkPaymentId;
    private String sparkassePeriodicPaymentId;

    private String currentDate;

    @Override
    protected Application configure() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // your date (java.util.Date)
        cal.add(Calendar.DATE, 1); // You can -/+ x months
        currentDate = simpleDateFormat.format(cal.getTime());
        ResourceConfig config = setupFiltersAndErrorHandlers();

        return config.register(PaymentInitiationResource.class).register(PaymentStatusResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void getSinglePaymentStatusConsors() {
        Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + getConsorsSinglePaymentId() + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", CONSORS_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void getPeriodicPaymentStatusConsors() {
        Invocation invocation = target("/v1/periodic-payments/sepa-credit-transfers/" + getConsorsPeriodicPaymentId() + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", CONSORS_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void getSinglePaymentStatusSparkasse() {
        Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + getSparkasseSinglePaymentId() + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", SPARKASSE_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void getBulkPaymentStatusSparkasse() {
        Invocation invocation = target("/v1/bulk-payments/sepa-credit-transfers/" + getSparkasseBulkPaymentId() + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", SPARKASSE_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void getPeriodicPaymentStatusSparkasse() {
        Invocation invocation = target("/v1/periodic-payments/sepa-credit-transfers/" + getSparkassePeriodicPaymentId() + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", SPARKASSE_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

    private String getConsorsSinglePaymentId() {
        if (consorsSinglePaymentId == null) {
            Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", ACCESS_TOKEN);
            invocationBuilder.header("PSU-ID", "PSU-Successful");
            invocationBuilder.header("PSU-BIC", CONSORS_BIC);
            invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
            invocationBuilder.header("redirectPreferred", true);

            Jsonb jsonb = JsonbBuilder.create();
            SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

            Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);

            try {
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                consorsSinglePaymentId = paymentResponse.getPaymentId();
            } catch (IOException e) {
                return "";
            }
        }
        return consorsSinglePaymentId;
    }

    private String getConsorsPeriodicPaymentId() {
        if (consorsPeriodicPaymentId == null) {
            Jsonb jsonb = JsonbBuilder.create();
            PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", PeriodicPaymentInitiation.class);

            Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", ACCESS_TOKEN);
            invocationBuilder.header("PSU-ID", "PSU-Successful");
            invocationBuilder.header("PSU-BIC", CONSORS_BIC);
            invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
            invocationBuilder.header("redirectPreferred", true);
            Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);
            try {
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                consorsPeriodicPaymentId = paymentResponse.getPaymentId();
            } catch (IOException e) {
                return "";
            }
        }
        return consorsPeriodicPaymentId;
    }

    private String getSparkasseSinglePaymentId() {
        if (sparkasseSinglePaymentId == null) {
            Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", ACCESS_TOKEN);
            invocationBuilder.header("PSU-ID", "PSU-Successful");
            invocationBuilder.header("PSU-BIC", SPARKASSE_BIC);
            invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
            invocationBuilder.header("redirectPreferred", true);

            Jsonb jsonb = JsonbBuilder.create();
            SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE86999999990000001000\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE75999999990000001004\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"}]}", SinglePaymentInitiation.class);

            Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);

            try {
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                sparkasseSinglePaymentId = paymentResponse.getPaymentId();
            } catch (IOException e) {
                return "";
            }
        }
        return sparkasseSinglePaymentId;
    }

    private String getSparkasseBulkPaymentId() {
        if (sparkasseBulkPaymentId == null) {

            Invocation.Builder invocationBuilder = target("/v1/bulk-payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", ACCESS_TOKEN);
            invocationBuilder.header("PSU-ID", "PSU-Successful");
            invocationBuilder.header("PSU-BIC", SPARKASSE_BIC);
            invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
            invocationBuilder.header("redirectPreferred", true);


            Jsonb jsonb = JsonbBuilder.create();
            BulkPaymentInitiation bulkPaymentInitiation = jsonb.fromJson("{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE86999999990000001000\", \"name\":\"NOTPROVIDED\"},\"payments\":[{\"endToEndIdentification\": \"endToEndId\", \"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE75999999990000001004\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"},{\"endToEndIdentification\": \"endToEndId\", \"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE86999999990000001000\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"32.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE75999999990000001004\"},\"creditorName\":\"Gimmeyourmoney\",\"remittanceInformationUnstructured\":\"Myothervwz\"}]}", BulkPaymentInitiation.class);
            Invocation invocation = invocationBuilder.buildPost(Entity.entity(bulkPaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);
            try {
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                sparkasseBulkPaymentId = paymentResponse.getPaymentId();
            } catch (IOException e) {
                return "";
            }
        }
        return sparkasseBulkPaymentId;
    }

    private String getSparkassePeriodicPaymentId() {
        if (sparkassePeriodicPaymentId == null) {

            Jsonb jsonb = JsonbBuilder.create();
            PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson("{\"endToEndIdentification\": \"endToEndId\", \"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE86999999990000001000\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE75999999990000001004\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\"}]}", PeriodicPaymentInitiation.class);

            Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", ACCESS_TOKEN);
            invocationBuilder.header("PSU-ID", "PSU-1234");
            invocationBuilder.header("PSU-BIC", SPARKASSE_BIC);
            invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
            invocationBuilder.header("redirectPreferred", true);
            Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);

            try {
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                sparkassePeriodicPaymentId = paymentResponse.getPaymentId();
            } catch (IOException e) {
                return "";
            }
        }
        return sparkassePeriodicPaymentId;
    }

}
