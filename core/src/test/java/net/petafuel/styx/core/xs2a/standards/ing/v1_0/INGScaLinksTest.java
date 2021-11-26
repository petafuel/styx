package net.petafuel.styx.core.xs2a.standards.ing.v1_0;


import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class INGScaLinksTest {

    @Test
    void testHrefSerializer() {
        String body = "{\n" +
                "    \"links\": {\n" +
                "        \"scaRedirect\": {\n" +
                "            \"href\": \"https://myaccount.ing.com/payment-initiation/c985e35c-61a3-4b32-85fa-e7b39228c361/XX\"\n" +
                "        },\n" +
                "        \"self\": {\n" +
                "            \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/c985e35c-61a3-4b32-85fa-e7b39228c361\"\n" +
                "        },\n" +
                "        \"status\": {\n" +
                "            \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/c985e35c-61a3-4b32-85fa-e7b39228c361/status\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"paymentId\": \"c985e35c-61a3-4b32-85fa-e7b39228c361\",\n" +
                "    \"transactionStatus\": \"RCVD\"\n" +
                "}";
        String bodyWithHref = "{\n" +
                "    \"links\": {\n" +
                "        \"scaRedirect\": {\n" +
                "            \"href\": \"https://idp-mock.sparda-n.de.schulung.sparda.de/oauth2/authorize?bic=TEST7999&client_id=PSDDE-BAFIN-125314&redirect_uri=https://preprod-styx.paycenter.de:8450/v1/callbacks/payment/ok/289de4c3-76fd-4b36-8cfa-d95ea955693d&response_type=code&scope=PIS:tx-fc639818c9a9b2ee2963e8695ae340da567b8116481479ad7c56956156c1e855&code_challenge_method=S256&code_challenge=5oLRnVEZKP0n9mh6v5FDqQZ04kEuhVa2tN2egP3YlD4&state=52322620-33d5-42f4-a6d9-13e73fbce5e9\"\n" +
                "        },\n" +
                "        \"scaStatus\": {\n" +
                "            \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/s0wPCzRdC2il1QR_lqMI5X7Z6X9GRdeytUZ5cUaaOiCTs1rXbVNS4zgWoUl2-cHCjOC-zAu3SObPSQr46D3RMME8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/authorisations/be0cf6a8-29ad-4fac-acd3-7486bb940529\"\n" +
                "        },\n" +
                "        \"self\": {\n" +
                "            \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/s0wPCzRdC2il1QR_lqMI5X7Z6X9GRdeytUZ5cUaaOiCTs1rXbVNS4zgWoUl2-cHCjOC-zAu3SObPSQr46D3RMME8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\"\n" +
                "        },\n" +
                "        \"status\": {\n" +
                "            \"href\": \"https://preprod-styx.paycenter.de:8452/v1/payments/sepa-credit-transfers/s0wPCzRdC2il1QR_lqMI5X7Z6X9GRdeytUZ5cUaaOiCTs1rXbVNS4zgWoUl2-cHCjOC-zAu3SObPSQr46D3RMME8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/status\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"paymentId\": \"s0wPCzRdC2il1QR_lqMI5X7Z6X9GRdeytUZ5cUaaOiCTs1rXbVNS4zgWoUl2-cHCjOC-zAu3SObPSQr46D3RMME8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\",\n" +
                "    \"transactionStatus\": \"RCVD\"\n" +
                "}";

        Jsonb jsonb = JsonbBuilder.create();
        InitiatedPayment payment1 = jsonb.fromJson(body, InitiatedPayment.class);
        InitiatedPayment payment2 = jsonb.fromJson(bodyWithHref, InitiatedPayment.class);
        Assertions.assertNotNull(payment1.getPaymentId());
        Assertions.assertNotNull(payment1.getLinks().getScaRedirect());
        Assertions.assertNotNull(payment2.getPaymentId());
        Assertions.assertNotNull(payment2.getLinks().getScaRedirect());

    }
}
