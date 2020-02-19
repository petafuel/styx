package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
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

    public static final String CONSORS_SINGLE_PAYMNET_ID = "Y013Rxs6NQpWrN4liYRxS7-UqSNxhyrqLTkbc1fpnTz1g--XTWerxErm1dzxFK2jo9cuILEZoaqwlJeWqZS9Xg==_=_bS6p6XvTWI";
    public static final String CONSORS_PERIODIC_PAYMENT_ID = "KRYdnABpsmpHTEThBms_Mi5u95oAcDfoT5VSeWn1ZBcE1J7sfHZXNQ3WZDARblDx8QDEc-obd0buQu1rCOLoQA==_=_bS6p6XvTWI";
    public static final String CONSORS_BIC = "CSDBDE71";
    public static final String ACCESS_TOKEN = "d0b10916-7926-4b6c-a90c-3643c62e4b08";

    private String consorsSinglePaymentId;
    private String consorsPeriodicPaymentId;

    private String currentDate;

    @Override
    protected Application configure() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // your date (java.util.Date)
        cal.add(Calendar.DATE, 1); // You can -/+ x months
        currentDate = simpleDateFormat.format(cal.getTime());
        ResourceConfig config = setupFiltersAndErrorHandlers();

        return config
                .register(PaymentInitiationResource.class)
                .register(PaymentStatusResource.class);
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
        return  consorsSinglePaymentId;
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
        Invocation invocation = target("/v1/periodic-payments/sepa-credit-transfers/" + CONSORS_PERIODIC_PAYMENT_ID + "/status").request()
                .header("token", ACCESS_TOKEN)
                .header("PSU-ID", "PSU-Successful")
                .header("PSU-BIC", CONSORS_BIC)
                .header("PSU-IP-Address", "192.168.8.78")
                .buildGet();
        Response response = invocation.invoke(Response.class);
        Assertions.assertEquals(200, response.getStatus());
    }

//    @Test
//    @Category(IntegrationTest.class)
//    public void getSinglePaymentStatusFiducia() {
//        Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + CONSORS_SINGLE_PAYMNET_ID + "/status").request()
//                .header("token", ACCESS_TOKEN)
//                .header("PSU-ID", "PSU-Successful")
//                .header("PSU-BIC", CONSORS_BIC)
//                .header("PSU-IP-Address", "192.168.8.78")
//                .buildGet();
//        Response response = invocation.invoke(Response.class);
//        Assertions.assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    @Category(IntegrationTest.class)
//    public void getBulkPaymentStatusFiducia() {
//        Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + CONSORS_SINGLE_PAYMNET_ID + "/status").request()
//                .header("token", ACCESS_TOKEN)
//                .header("PSU-ID", "PSU-Successful")
//                .header("PSU-BIC", CONSORS_BIC)
//                .header("PSU-IP-Address", "192.168.8.78")
//                .buildGet();
//        Response response = invocation.invoke(Response.class);
//        Assertions.assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    @Category(IntegrationTest.class)
//    public void getPeriodicPaymentStatusFiducia() {
//        Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + CONSORS_SINGLE_PAYMNET_ID + "/status").request()
//                .header("token", ACCESS_TOKEN)
//                .header("PSU-ID", "PSU-Successful")
//                .header("PSU-BIC", CONSORS_BIC)
//                .header("PSU-IP-Address", "192.168.8.78")
//                .buildGet();
//        Response response = invocation.invoke(Response.class);
//        Assertions.assertEquals(200, response.getStatus());
//    }
}
