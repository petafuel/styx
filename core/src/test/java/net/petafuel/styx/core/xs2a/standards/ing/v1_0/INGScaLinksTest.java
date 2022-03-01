package net.petafuel.styx.core.xs2a.standards.ing.v1_0;


import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;


class INGScaLinksTest {

    @Test
    void testHrefSerializer() {
        String body = "{\n" +
                "  \"_links\": {\n" +
                "    \"scaRedirect\": \"https://myaccount.ing.com/payment-initiation/023feba0-ce9b-4f6b-be09-0bff281ccb55/XX\",\n" +
                "    \"self\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/023feba0-ce9b-4f6b-be09-0bff281ccb55\",\n" +
                "    \"status\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/023feba0-ce9b-4f6b-be09-0bff281ccb55/status\"\n" +
                "  },\n" +
                "  \"paymentId\": \"023feba0-ce9b-4f6b-be09-0bff281ccb55\",\n" +
                "  \"transactionStatus\": \"RCVD\"\n" +
                "}\n";
        String bodyWithHref = "{\n" +
                "  \"_links\": {\n" +
                "    \"scaRedirect\": {\n" +
                "      \"href\": \"https://idp-mock.sparda-n.de.schulung.sparda.de/oauth2/authorize?bic=TEST7999&client_id=PSDDE-BAFIN-125314&redirect_uri=https://preprod-styx.paycenter.de:8450/v1/callbacks/payment/ok/79b19aae-2a6e-4afe-889d-dfe32190fad8&response_type=code&scope=PIS:tx-d6e3b38f7456cd1f2a7088c5588a603a282c5486001c8e50076641256efef369&code_challenge_method=S256&code_challenge=0Tk_vTrN56Lo9tngmcrwqnJDlDY5V3v4jnqfM2b3_bY&state=a6e76223-06b7-490c-a4fa-6cbef4b85c36\"\n" +
                "    },\n" +
                "    \"scaStatus\": {\n" +
                "      \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/35ngApqKH9d5icGeUcMSlqT5DA9svEL8n0HXL8sqkcBv_IV4muLvF3J7pWTqrZzQh623L3yTr2un-vqsP38W38E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/authorisations/a94c348e-0bd1-416a-a5c2-8dbf15fbdbfd\"\n" +
                "    },\n" +
                "    \"self\": {\n" +
                "      \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/35ngApqKH9d5icGeUcMSlqT5DA9svEL8n0HXL8sqkcBv_IV4muLvF3J7pWTqrZzQh623L3yTr2un-vqsP38W38E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\"\n" +
                "    },\n" +
                "    \"status\": {\n" +
                "      \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/35ngApqKH9d5icGeUcMSlqT5DA9svEL8n0HXL8sqkcBv_IV4muLvF3J7pWTqrZzQh623L3yTr2un-vqsP38W38E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/status\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"paymentId\": \"35ngApqKH9d5icGeUcMSlqT5DA9svEL8n0HXL8sqkcBv_IV4muLvF3J7pWTqrZzQh623L3yTr2un-vqsP38W38E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\",\n" +
                "  \"transactionStatus\": \"RCVD\"\n" +
                "}";

        Jsonb jsonb = JsonbBuilder.create();
        InitiatedPayment payment1 = jsonb.fromJson(body, InitiatedPayment.class);
        InitiatedPayment payment2 = jsonb.fromJson(bodyWithHref, InitiatedPayment.class);
        Assertions.assertNotNull(payment1.getPaymentId());
        Assertions.assertNotNull(payment1.getLinks().getScaRedirect());
        Assertions.assertEquals("https://myaccount.ing.com/payment-initiation/023feba0-ce9b-4f6b-be09-0bff281ccb55/XX", payment1.getLinks().getScaRedirect().getUrl());
        Assertions.assertNotNull(payment2.getPaymentId());
        Assertions.assertNotNull(payment2.getLinks().getScaRedirect());
        Assertions.assertEquals("https://idp-mock.sparda-n.de.schulung.sparda.de/oauth2/authorize?bic=TEST7999&client_id=PSDDE-BAFIN-125314&redirect_uri=https://preprod-styx.paycenter.de:8450/v1/callbacks/payment/ok/79b19aae-2a6e-4afe-889d-dfe32190fad8&response_type=code&scope=PIS:tx-d6e3b38f7456cd1f2a7088c5588a603a282c5486001c8e50076641256efef369&code_challenge_method=S256&code_challenge=0Tk_vTrN56Lo9tngmcrwqnJDlDY5V3v4jnqfM2b3_bY&state=a6e76223-06b7-490c-a4fa-6cbef4b85c36", payment2.getLinks().getScaRedirect().getUrl());

    }
}
