package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.PeriodicPaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PeriodicPaymentResourceRaiffeisenMSTest extends StyxRESTTest {
    private static final String PSU_ID = "EndToEndId";
    private static final String BIC = "GENODEF1M03";
    private static String paymentId;
    private static String debtorIBAN = "DE60760300800500123456";
    private static String creditorName = "Hans Handbuch";
    private static String creditorIBAN = "DE98701204008538752000";
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
                .register(FetchPaymentResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void A_initiatePeriodicPayment() {

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        String requestBody = "{\"startDate\":\"" + currentDate + "\",\"dayOfExecution\":31,\"frequency\":\"MNTH\",\"executionRule\":\"following\",\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"" + debtorIBAN +"\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"1.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"" + creditorIBAN + "\"},\"creditorName\":\"" + creditorName + "\",\"remittanceInformationUnstructured\":\"Test\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}";
        PeriodicPaymentInitiation periodicPaymentInitiation = jsonb.fromJson(requestBody, PeriodicPaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(periodicPaymentInitiation, MediaType.APPLICATION_JSON));
        PaymentResponse response = invocation.invoke(PaymentResponse.class);
        Assertions.assertNotNull(response.getPaymentId());
        paymentId = response.getPaymentId();
    }

    @Test
    @Category(IntegrationTest.class)
    public void B_getPeriodicPayment() {

        Invocation.Builder invocationBuilder = target("/v1/periodic-payments/sepa-credit-transfers/" + paymentId).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");

        Invocation invocation = invocationBuilder.buildGet();
        InitializablePayment response = invocation.invoke(PeriodicPayment.class);
        Assertions.assertNotNull(response instanceof PeriodicPayment);
    }

}
