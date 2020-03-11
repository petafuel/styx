package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.api.v1.payment.entity.StartSCARequest;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.runners.MethodSorters;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SCAPaymentResourceTest extends StyxRESTTest {
    private static String paymentId;
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
                .register(PaymentAuthorisationResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void A_initiateSinglePayment_Targo() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSD2TEST2");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE60760300800500123456\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE15500105172295759744\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        PaymentResponse response = invocation.invoke(PaymentResponse.class);
        Assertions.assertNotNull(response.getPaymentId());
        SCAPaymentResourceTest.paymentId = response.getPaymentId();
    }

    @Test
    @Category(IntegrationTest.class)
    public void B_startSCAwithAuthentication_Targo() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers/" + paymentId + "/authorisations").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSD2TEST2");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        PSUData psuData = new PSUData();
        psuData.setPassword("123456");
        StartSCARequest startSCARequest = new StartSCARequest();
        startSCARequest.setPsuData(psuData);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(startSCARequest, MediaType.APPLICATION_JSON));
        SCA response = invocation.invoke(SCA.class);
        Assertions.assertEquals(SCA.Status.PSUAUTHENTICATED, response.getScaStatus());
        Assertions.assertEquals(SCA.Approach.EMBEDDED, response.getApproach());
    }

    @Test
    @Category(IntegrationTest.class)
    public void C_startSCAwithoutAuthentication_Targo() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers/" + paymentId + "/authorisations").request();
        invocationBuilder.header("token", "d0b10916-7926-4b6c-a90c-3643c62e4b08");
        invocationBuilder.header("PSU-ID", "PSD2TEST2");
        invocationBuilder.header("PSU-BIC", "CMCIDEDD");
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);
        invocationBuilder.header("X-STYX-X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Invocation invocation = invocationBuilder.buildPost(Entity.entity("{}", MediaType.APPLICATION_JSON));
        SCA response = invocation.invoke(SCA.class);
        Assertions.assertEquals(SCA.Status.PSUIDENTIFIED, response.getScaStatus());
        Assertions.assertEquals(SCA.Approach.EMBEDDED, response.getApproach());
    }
}
