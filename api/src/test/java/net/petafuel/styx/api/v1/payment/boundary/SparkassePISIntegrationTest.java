package net.petafuel.styx.api.v1.payment.boundary;


import io.restassured.http.ContentType;
import net.petafuel.styx.api.AcceptanceTest;
import net.petafuel.styx.api.IntegrationTest;
import net.petafuel.styx.api.v1.payment.entity.SinglePaymentInitiation;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Amount;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@Category(IntegrationTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparkassePISIntegrationTest implements AcceptanceTest {
    private static String PIS_TOKEN;

    @BeforeClass
    public static void styxTokenSetup() {
        System.out.println("SparkassePISIntegrationTest ChildClass @BeforeAll");
        PIS_TOKEN = given()
                .contentType(ContentType.JSON)
                .header("token", System.getProperty(PROPERTY_MASTER_TOKEN))
                .header("service", XS2ATokenType.PIS.toString())
                .post("/v1/auth")
                .then()
                .assertThat()
                .header("content-type", Matchers.containsString("application/json"))
                .statusCode(200)
                .body("token", Matchers.notNullValue())
                .extract()
                .path("token");
        PIS_TOKEN = null;
    }

    @Category(IntegrationTest.class)
    @ParameterizedTest
    @ArgumentsSource(SparkassePSUProvider.class)
    void test_INIT_SCTSinglePayment_SCA_OAUTH(String psuBic, String psuId, String creditorIban, String debtorIban) {
        Assume.assumeNotNull(PIS_TOKEN);
        SinglePayment singlePayment = new SinglePayment();
        singlePayment.setDebtorAccount(new AccountReference(debtorIban, AccountReference.Type.IBAN));
        singlePayment.setCreditorAccount(new AccountReference(creditorIban, AccountReference.Type.IBAN));
        singlePayment.setInstructedAmount(new Amount("0.01"));
        singlePayment.setCreditorName(psuId);
        singlePayment.setRemittanceInformationUnstructured("Styx PIS Test unixstamp: " + new Date().getTime());

        SinglePaymentInitiation singlePaymentInitiation = new SinglePaymentInitiation();
        singlePaymentInitiation.setPayments(Collections.singletonList(singlePayment));

        given()
                .contentType(ContentType.JSON)
                .header("token", PIS_TOKEN)
                .header("psu-id", psuId)
                .header("psu-bic", psuBic)
                .header("redirectPreferred", "true")
                .body(singlePaymentInitiation)
                .post("/v1/payments/sepa-credit-transfers")
                .then()
                .assertThat()
                .header("content-type", Matchers.containsString("application/json"))
                .statusCode(201)
                .body("transactionStatus", Matchers.is(TransactionStatus.RCVD.name()))
                .body("paymentId", Matchers.notNullValue())
                .body("links.scaOAuth.href", Matchers.notNullValue());
    }

    @Category(IntegrationTest.class)
    @ParameterizedTest
    @ArgumentsSource(SparkassePSUProvider.class)
    void test_INIT_SCTSinglePayment_SCA_EMBEDDED(String psuBic, String psuId, String creditorIban, String debtorIban) {
        Assume.assumeNotNull(PIS_TOKEN);
        SinglePayment singlePayment = new SinglePayment();
        singlePayment.setDebtorAccount(new AccountReference(debtorIban, AccountReference.Type.IBAN));
        singlePayment.setCreditorAccount(new AccountReference(creditorIban, AccountReference.Type.IBAN));
        singlePayment.setInstructedAmount(new Amount("0.01"));
        singlePayment.setCreditorName(psuId);
        singlePayment.setRemittanceInformationUnstructured("Styx PIS Test unixstamp: " + new Date().getTime());

        SinglePaymentInitiation singlePaymentInitiation = new SinglePaymentInitiation();
        singlePaymentInitiation.setPayments(Collections.singletonList(singlePayment));

        given()
                .contentType(ContentType.JSON)
                .header("token", PIS_TOKEN)
                .header("psu-id", psuId)
                .header("psu-bic", psuBic)
                .header("redirectPreferred", "false")
                .body(singlePaymentInitiation)
                .post("/v1/payments/sepa-credit-transfers")
                .then()
                .assertThat()
                .header("content-type", Matchers.containsString("application/json"))
                .statusCode(201)
                .body("transactionStatus", Matchers.is(TransactionStatus.RCVD.name()))
                .body("paymentId", Matchers.notNullValue())
                .body("links.startAuthorisationWithPsuAuthentication.href", Matchers.notNullValue());
    }

    static class SparkassePSUProvider implements ArgumentsProvider {
        /**
         * returns in format of bic, psu-id, creditor iban, debtor iban
         *
         * @param context interfacedefinition
         * @return multiple arrangements of sparkasse test data for sandbox environment
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("BYLADEM1FSI", "smsTAN_multiMed", "DE98999999990000009999", "DE86999999990000001000")
            );
        }
    }
}
