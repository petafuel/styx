package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.AuthorisationRequest;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
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
public class SinglePaymentAuthorisationResourceFiduciaTest extends StyxRESTTest {
    private static final String PSU_ID = "VRK1234567890ALL";
    private static final String PSU_PIN = "password";
    private static final String PSU_OTP = "123456";
    private static final String BIC = "GENODEF1M03";
    private static final String SCA_METHOD_ID = "942";
    private static String paymentId;
    private static String authorisationId;
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
    public void A_initiateSinglePayment() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        Jsonb jsonb = JsonbBuilder.create();
        SinglePaymentInitiation singlePaymentInitiation = jsonb.fromJson("{\"payments\":[{\"debtorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE39499999600000005111\"},\"instructedAmount\":{\"currency\":\"EUR\",\"amount\":\"520.00\"},\"creditorAccount\":{\"currency\":\"EUR\",\"iban\":\"DE18499999600000005101\"},\"creditorName\":\"WBG\",\"remittanceInformationUnstructured\":\"Ref.NumberWBG-1222\",\"requestedExecutionDate\":\"" + currentDate + "\"}]}", SinglePaymentInitiation.class);

        Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
        PaymentResponse response = invocation.invoke(PaymentResponse.class);
        Assertions.assertNotNull(response.getPaymentId());
        paymentId = response.getPaymentId();
    }

    @Test
    @Category(IntegrationTest.class)
    public void B_startSCAwithAuthentication() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers/" + paymentId + "/authorisations").request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        PSUData psuData = new PSUData();
        psuData.setPassword(PSU_PIN);
        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setPsuData(psuData);
        Invocation invocation = invocationBuilder.buildPost(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        SCA response = invocation.invoke(SCA.class);
        Assertions.assertEquals(SCA.Status.PSUAUTHENTICATED, response.getScaStatus());
        Assertions.assertEquals(SCA.Approach.EMBEDDED, response.getApproach());
        authorisationId = response.getAuthorisationId();
    }

    @Test
    @Category(IntegrationTest.class)
    public void C_selectSCAMethod() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers/" + paymentId + "/authorisations/" + authorisationId).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setAuthenticationMethodId(SCA_METHOD_ID);
        Invocation invocation = invocationBuilder.buildPut(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        SCA response = invocation.invoke(SCA.class);
        Assertions.assertEquals(SCA.Status.STARTED, response.getScaStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void D_authoriseTransactionWithTANOTP() {
        Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers/" + paymentId + "/authorisations/" + authorisationId).request();
        invocationBuilder.header("token", pisAccessToken);
        invocationBuilder.header("PSU-ID", PSU_ID);
        invocationBuilder.header("PSU-BIC", BIC);
        invocationBuilder.header("PSU-IP-Address", "192.168.8.78");
        invocationBuilder.header("redirectPreferred", true);

        AuthorisationRequest authorisationRequest = new AuthorisationRequest();
        authorisationRequest.setScaAuthenticationData(PSU_OTP);
        Invocation invocation = invocationBuilder.buildPut(Entity.entity(authorisationRequest, MediaType.APPLICATION_JSON));
        SCA response = invocation.invoke(SCA.class);
        Assertions.assertEquals(SCA.Status.FINALISED, response.getScaStatus());
    }
}
