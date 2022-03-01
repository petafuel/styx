package net.petafuel.styx.api.v1.payment.boundary;

import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.StyxRESTTest;
import net.petafuel.styx.api.v1.payment.entity.PaymentResponse;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Amount;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import org.apache.commons.io.IOUtils;
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
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UniCreditPISIntegrationTest extends StyxRESTTest {

    private static final List<Map<String, String>> testData = Arrays.asList(
            new HashMap<String, String>() {{
                put("bic", "HYVEDEMM488");
                put("debtorIban", "DE49700202700123456785");
                put("creditorIban", "AT231200056789012345");
            }}, new HashMap<String, String>() {{
                put("bic", "CAIBATWW");
                put("debtorIban", "AT231200056789012345");
                put("creditorIban", "DE49700202700123456785");
            }});

    @Override
    protected Application configure() {
        ResourceConfig config = setupFiltersAndErrorHandlers();
        return config
                .register(PaymentInitiationResource.class)
                .register(FetchPaymentResource.class)
                .register(PaymentStatusResource.class);
    }

    @Test
    @Category(IntegrationTest.class)
    public void createPayment() {

        for (Map<String, String> testEntry : testData) {

            String bic = testEntry.get("bic");
            String debtorIban = testEntry.get("debtorIban");
            String creditorIban = testEntry.get("creditorIban");

            Invocation.Builder invocationBuilder = target("/v1/payments/sepa-credit-transfers").request();
            invocationBuilder.header("token", pisAccessToken);
            invocationBuilder.header("PSU-ID", "bgdemo");
            invocationBuilder.header("PSU-BIC", bic);
            invocationBuilder.header("PSU-ID-Type", "ALL");
            invocationBuilder.header("redirectPreferred", true);

            SinglePayment singlePayment = new SinglePayment();
            singlePayment.setDebtorAccount(new AccountReference(debtorIban, AccountReference.Type.IBAN));
            singlePayment.setCreditorAccount(new AccountReference(creditorIban, AccountReference.Type.IBAN));
            singlePayment.setInstructedAmount(new Amount("0.01"));
            singlePayment.setCreditorName("Test Creditor Name");
            singlePayment.setRemittanceInformationUnstructured("Styx PIS Test mit Umlaut ÄöÜ");

            SinglePaymentInitiation singlePaymentInitiation = new SinglePaymentInitiation();
            singlePaymentInitiation.setPayments(Collections.singletonList(singlePayment));

            Invocation invocation = invocationBuilder.buildPost(Entity.entity(singlePaymentInitiation, MediaType.APPLICATION_JSON));
            Response response = invocation.invoke(Response.class);
            Assertions.assertEquals(201, response.getStatus());

            try {
                Jsonb jsonb = JsonbBuilder.create();
                PaymentResponse paymentResponse = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentResponse.class);
                String singlePaymentId = paymentResponse.getPaymentId();
                Assertions.assertNotNull(singlePaymentId);
                testEntry.put("paymentId", singlePaymentId);
            } catch (IOException e) {
                Assertions.fail("exception while parsing response");
            }
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void getSinglePayment() throws IOException {

        for (Map<String, String> testEntry : testData) {

            String singlePaymentId = testEntry.get("paymentId");
            String bic = testEntry.get("bic");

            Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + singlePaymentId).request()
                    .header("token", pisAccessToken)
                    .header("PSU-ID", "bgdemo")
                    .header("PSU-BIC", bic)
                    .buildGet();
            Response response = invocation.invoke(Response.class);
            Assertions.assertEquals(200, response.getStatus());

            Jsonb jsonb = JsonbBuilder.create();
            SinglePayment payment = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), SinglePayment.class);
            Assertions.assertNotNull(payment.getCreditorAccount().getCurrency());
            Assertions.assertNotNull(payment.getCreditorAccount().getIban());
            Assertions.assertNotNull(payment.getCreditorName());
            Assertions.assertNotNull(payment.getDebtorAccount().getCurrency());
            Assertions.assertNotNull(payment.getDebtorAccount().getIban());
            Assertions.assertNotNull(payment.getInstructedAmount().getAmount());
            Assertions.assertNotNull(payment.getInstructedAmount().getCurrency());
            Assertions.assertNotNull(payment.getRemittanceInformationUnstructured());
            Assertions.assertEquals("Styx PIS Test mit Umlaut ÄöÜ", payment.getRemittanceInformationUnstructured());
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void getSinglePaymentStatus() throws IOException {

        for (Map<String, String> testEntry : testData) {

            String singlePaymentId = testEntry.get("paymentId");
            String bic = testEntry.get("bic");

            Invocation invocation = target("/v1/payments/sepa-credit-transfers/" + singlePaymentId + "/status").request()
                    .header("token", pisAccessToken)
                    .header("PSU-ID", "bgdemo")
                    .header("PSU-BIC", bic)
                    .buildGet();
            Response response = invocation.invoke(Response.class);
            Assertions.assertEquals(200, response.getStatus());

            Jsonb jsonb = JsonbBuilder.create();
            PaymentStatus paymentStatus = jsonb.fromJson(IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8), PaymentStatus.class);
            Assertions.assertEquals(TransactionStatus.RCVD.getName(), paymentStatus.getTransactionStatus().getName());
        }
    }

}
