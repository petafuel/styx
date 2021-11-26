package net.petafuel.styx.core.xs2a.standards.ing.v1_0;


import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class INGScaLinksTest {

    @Test
    void testHrefSerializer() {
        String body = "{ \"transactionStatus\": \"RCVD\", \"paymentId\": \"37e92610-4e99-4c84-a15a-0c500be57440\", \"_links\":{ \"scaRedirect\":\"https://myaccount.ing.com/payment-initiation/37e92610-4e99-4c84-a15a-0c500be57440/XX\", \"self\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440\", \"status\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440/status\", \"delete\":\"https://api.ing.com/v1/payments/sepa-credit-transfers/37e92610-4e99-4c84-a15a-0c500be57440\" } }";
        String bodyWithHref = "{\"transactionStatus\":\"RCVD\",\"paymentId\":\"I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\",\"_links\":{\"scaRedirect\":{\"href\":\"https://idp-mock.sparda-n.de.schulung.sparda.de/oauth2/authorize?bic=TEST7999&client_id=PSDDE-BAFIN-125314&redirect_uri=https://preprod-styx.paycenter.de:8450/v1/callbacks/payment/ok/9576ac9d-8915-40e1-b8a9-bfd22aadbf47&response_type=code&scope=PIS:tx-6d1df3fec71a02567bab0876cf65616e88c9467f0af55a3c5175e5d89d032753&code_challenge_method=S256&code_challenge=lRPHsFD6rWW1zJlodkYWMRdV0K9uY29EXe_L7ZM_SZc\"},\"self\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q\"},\"status\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/status\"},\"scaStatus\":{\"href\":\"https://api.sparda.de/xs2a/3.0.0/v1/payments/sepa-credit-transfers/I9xBzY1sc22eewskF4yTo53XMAH6q9M2acokk3LRspv2rp75SPnKXaDCyp2TXAPJrSQXvawFMDq2VFAR-wgps8E8ZcgkupoAptjViDqg52c=_=_psGLvQpt9Q/authorisations/fb49667c-3ce3-4170-b62b-17fbcac3c055\"}},\"scaStatus\":\"received\"}";

        Jsonb jsonb = JsonbBuilder.create();
        InitiatedPayment payment1 = jsonb.fromJson(body, InitiatedPayment.class);
        InitiatedPayment payment2 = jsonb.fromJson(bodyWithHref, InitiatedPayment.class);
        Assertions.assertNotNull(payment1.getPaymentId());
        Assertions.assertNotNull(payment1.getLinks().getScaRedirect());
        Assertions.assertNotNull(payment2.getPaymentId());
        Assertions.assertNotNull(payment2.getLinks().getScaRedirect());

    }
}
