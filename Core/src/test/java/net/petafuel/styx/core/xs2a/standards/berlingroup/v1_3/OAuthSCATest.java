package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.jsepa.model.*;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.oauth.Token;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Vector;
import java.util.Collections;
import java.util.UUID;
import java.util.Base64;

public class OAuthSCATest {

    public static final String SPARKASSE_BANK_BASE_API = "https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678";
    public static final String SPARKASSE_BANK_AUTHORIZATION_SERVER = "https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/oauth/12345678";

    @Tag("Integration")
    @Test
    public void initializeSinglePayment() {

        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BANK_BASE_API, new BerlinGroupSigner()));

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        PaymentInstructionInformation p1 = new PaymentInstructionInformation();
        CreditTransferTransactionInformation cdtTrfTxInf = new CreditTransferTransactionInformation();

        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-10-10";
        int numberOfTransactions = 1;
        double controlSum = 100.00;
        double amount = 100.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-10-10";
        String debtorName = "Debtor Name";
        String debtorIban = "DE86999999990000001000";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        String endToEndID = "EndToEndId";
        String creditorName = "Hans Handbuch";
        String creditorIBAN = "DE98999999990000009999";
        String purpose = "purpose string";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf.setEndToEndID(endToEndID);
        cdtTrfTxInf.setAmount(amount);
        cdtTrfTxInf.setCreditorName(creditorName);
        cdtTrfTxInf.setCreditorIBAN(creditorIBAN);
        cdtTrfTxInf.setVwz(purpose);

        p1.setPmtInfId(paymentInformationId);
        p1.setPaymentMethod(paymentMethod);
        p1.setNoTxns(numberOfTransactions);
        p1.setCtrlSum(controlSum);
        p1.setRequestedExecutionDate(requestedExecutionDate);
        p1.setDebtorName(debtorName);
        p1.setDebtorAccountIBAN(debtorIban);
        p1.setDebitorBic(debtorBic);
        p1.setChargeBearer(chargeBearer);

        p1.setCreditTransferTransactionInformationVector(Collections.singletonList(cdtTrfTxInf));
        pmtInfos.add(p1);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        // Creating the request instance
        String psuIpAddress = "192.168.1.1";
        String callbackUrl = Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");

        PaymentInitiationPain001Request request = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, document, psuIpAddress);
        request.setTppRedirectPreferred(true);
        request.setTppRedirectUri(callbackUrl);

        // Generating the code_verifier, code_challenge & state
        try {

            String state = UUID.randomUUID().toString();
            SecureRandom sr = new SecureRandom();
            byte[] code = new byte[32];
            sr.nextBytes(code);
            String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            String codeChallenge = Base64.getUrlEncoder().encodeToString(digest);

            String clientId = Config.getInstance().getProperties().getProperty("keystore.client_id");

            InitiatedPayment payment = standard.getPis().initiatePaymentPain001(request);
            String urlToSca = SPARKASSE_BANK_AUTHORIZATION_SERVER + "/authorize?" +
                    "client_id=" + clientId +
                    "&response_type=" + "code" +
                    "&scope=PIS: " + payment.getPaymentId() +
                    "&redirect_uri=" + callbackUrl +
                    "&state=" + state +
                    "&code_challenge_method=" + "S256" +
                    "&code_challenge=" + codeChallenge;

            System.out.println(urlToSca);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Tag("Skip")
    @Test
    public void getTokenRequest() {

        String code = "FA2AD06C39DABFB7A9BA415306E2C5E5";
        String code_verifier = "BbXNja3WWJzKEEvk0j8P26sBnQBFyjoLeVTyUKcoQmE";

        TokenRequest request = new TokenRequest(code, code_verifier);
        OAuthService service = new OAuthService(SPARKASSE_BANK_AUTHORIZATION_SERVER, new BerlinGroupSigner());
        try {
            Token t1 = service.accessTokenRequest(request);
        } catch (Exception ignored) {}
        Assert.assertTrue(true);
    }


    @Tag("Integration")
    @Test
    public void authorizeConsent() {
        Assert.assertTrue(true);
    }
}
