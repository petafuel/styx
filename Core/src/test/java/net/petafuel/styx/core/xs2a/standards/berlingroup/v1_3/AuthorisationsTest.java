package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.AuthenticationObject;
import net.petafuel.styx.core.xs2a.entities.AuthenticationType;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AuthorisationsTest {

    private static final String SPARKASSE_BIC = "BYLADEM1FSI";
    private static final String SPARKASSE_PIN = "okok1";
    private static final String SPARKASSE_PSU_ID = "smsTAN_singleMed";
    private static final String SPARKASSE_IBAN = "DE86999999990000001000";

    private static final String TARGO_PSU_ID = "PSD2TEST2";
    private static final String TARGO_PSU_IP_ADDRESS = "192.168.8.78";
    private static final String TARGO_BIC = "CMCIDEDD";
    private static final String TARGO_STATIC_API_TOKEN = "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE";

    private static final String FIDUCIA_BIC = "GENODEF1M03";
    private static final String FIDUCIA_PSU_ID = "VRK1234567890SMS";
    private static final String FIDUCIA_PIN = "password";

    @Test
    @Tag("integration")
    public void startAuthorisationSparkasseConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(SPARKASSE_BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        Consent consent = this.getSparkasseConsent(standard.getCs());

        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(), consent.getId());
        request.getPsu().setId(SPARKASSE_PSU_ID);

        if (consent.getSca().getLinks().getUrlMapping().containsKey(LinkType.AUTHORISATION_WITH_PSU_AUTHENTICATION)) {
            PSUData psuData = new PSUData();
            psuData.setPassword(SPARKASSE_PIN);
            request.setPsuData(psuData);
        }

        SCA sca = standard.getCs().startAuthorisation(request);
        Assert.assertNotNull(sca.getAuthorisationId());
    }

    @Test
    @Tag("integration")
    public void getCSAuthorisationAndSCAStatusSparkasse() throws BankRequestFailedException, BankNotFoundException, BankLookupFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(SPARKASSE_BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        PSUData psuData = new PSUData();
        psuData.setPassword(SPARKASSE_PIN);

        Consent consent = this.getSparkasseConsent(standard.getCs());
        StartAuthorisationRequest request = new StartAuthorisationRequest(psuData, consent.getId());
        request.getPsu().setId("smsTAN_singleMed");

        SCA sca = standard.getCs().startAuthorisation(request);

        GetAuthorisationsRequest getAuthorisationRequest = new GetAuthorisationsRequest(consent.getId());

        GetSCAStatusRequest getSCAStatusRequest = new GetSCAStatusRequest(consent.getId(), sca.getAuthorisationId());
        getSCAStatusRequest.getPsu().setId("smsTAN_singleMed");
        List<String> authIdList = standard.getCs().getAuthorisations(getAuthorisationRequest);

        SCA.Status scaStatus = standard.getCs().getSCAStatus(getSCAStatusRequest);

        Assert.assertEquals(scaStatus, sca.getScaStatus());
        Assert.assertTrue(authIdList.contains(sca.getAuthorisationId()));
    }

    @Test
    @Tag("integration")
    public void startAuthorisationTargoPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSU(TARGO_PSU_ID), new PSUData(), PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payment.getPaymentId());

        SCA sca = standard.getPis().startAuthorisation(request);
        Assert.assertNotNull(sca.getAuthorisationId());
    }

    @Test
    @Tag("integration")
    public void getPISAuthorisationAndSCAStatusTargo() throws BankRequestFailedException, BankNotFoundException, BankLookupFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSU(TARGO_PSU_ID), new PSUData(), PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payment.getPaymentId());

        SCA sca = standard.getPis().startAuthorisation(request);

        GetAuthorisationsRequest getAuthorisationRequest = new GetAuthorisationsRequest(PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payment.getPaymentId());

        GetSCAStatusRequest getSCAStatusRequest = new GetSCAStatusRequest(PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payment.getPaymentId(), sca.getAuthorisationId());

        List<String> authIdList = standard.getPis().getAuthorisations(getAuthorisationRequest);

        SCA.Status scaStatus = standard.getPis().getSCAStatus(getSCAStatusRequest);

        Assert.assertEquals(scaStatus, sca.getScaStatus());
        Assert.assertTrue(authIdList.contains(sca.getAuthorisationId()));
    }

    @Test
    @Tag("integration")
    public void startAuthorisationConsentFiducia() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {

        XS2AStandard standard = (new SAD()).getBankByBIC(FIDUCIA_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        Consent consent = this.getConsentFiducia(standard.getCs());
        StartAuthorisationRequest request = new StartAuthorisationRequest(null, consent.getId());
        request.getPsu().setId(FIDUCIA_PSU_ID);

        if (consent.getSca().getLinks().getUrlMapping().containsKey(LinkType.AUTHORISATION_WITH_PSU_AUTHENTICATION)) {
            PSUData psuData = new PSUData();
            psuData.setPassword(FIDUCIA_PIN);
            request.setPsuData(psuData);
        }

        SCA sca = standard.getPis().startAuthorisation(request);
        Assert.assertNotNull(sca.getChallengeData());
    }

    private Consent getSparkasseConsent(CSInterface csInterface) throws BankRequestFailedException {

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference(SPARKASSE_IBAN, AccountReference.Type.IBAN));

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference(SPARKASSE_IBAN, AccountReference.Type.IBAN));

        PSU psu = new PSU(SPARKASSE_PSU_ID);
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 2);
        consent.setValidUntil(calendar.getTime());
        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.setTppRedirectPreferred(false);

        return csInterface.createConsent(createConsentRequest);
    }


    private InitiatedPayment getTargoPayment(PISInterface pisInterface) throws BankRequestFailedException {
        //payment information
        String creditorIban = "DE70300209005320320678";
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE70300209005320320678";
        Currency debtorCurrency = Currency.EUR;
        String amount = "0.99";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Beispiel Verwendungszweck";

        Payment paymentBody = new Payment();
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditorName(creditorName);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setInstructedAmount(new InstructedAmount(amount, instructedCurrency));
        paymentBody.setRemittanceInformationUnstructured(reference);

        PSU psu = new PSU(TARGO_PSU_ID);
        psu.setIp(TARGO_PSU_IP_ADDRESS);
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(false);
        request.getHeaders().put("X-bvpsd2-test-apikey", TARGO_STATIC_API_TOKEN);

        return pisInterface.initiatePayment(request);
    }

    public Consent getConsentFiducia(CSInterface csInterface) throws BankRequestFailedException {

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE40100100103307118608", AccountReference.Type.IBAN));

        PSU psu = new PSU(FIDUCIA_PSU_ID);
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 2);
        consent.setValidUntil(calendar.getTime());

        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        return csInterface.createConsent(createConsentRequest);

    }


    @Test
    @DisplayName("[Targobank] start Authorisation, update PSU Authentication, select SCA Method, authroise Transaction")
    @Tag("integration")
    public void finalizePaymentTargo() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);
        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSU(TARGO_PSU_ID), new PSUData(), PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS, payment.getPaymentId());

        SCA scaStarted = standard.getPis().startAuthorisation(request);
        Assertions.assertEquals(SCA.Status.PSUIDENTIFIED, scaStarted.getScaStatus());
        System.out.println("Authorisation started -> PSU Identified");

        PSUData psuData = new PSUData();
        psuData.setPassword("123456");
        SCA updatedPSUAuthentication = standard.getPis().updatePSUAuthentication(new UpdatePSUAuthenticationRequest(new PSU(TARGO_PSU_ID),
                psuData,
                PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS,
                payment.getPaymentId(),
                scaStarted.getAuthorisationId()));
        Assertions.assertEquals(SCA.Status.PSUAUTHENTICATED, updatedPSUAuthentication.getScaStatus());
        System.out.println("PSUData pushed to ASPSP -> PSU Authenticated");

        String scaMethodId = null;
        for (AuthenticationObject authObj : updatedPSUAuthentication.getScaMethods()) {
            if (authObj.getAuthenticationType() == AuthenticationType.SMS_OTP) {
                scaMethodId = authObj.getAuthenticationMethodId();
                break;
            }
        }
        SCA selectedSCAMethod = standard.getPis().selectAuthenticationMethod(new SelectAuthenticationMethodRequest(scaMethodId,
                PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS,
                payment.getPaymentId(),
                scaStarted.getAuthorisationId()));
        Assertions.assertEquals(SCA.Status.SCAMETHODSELECTED, selectedSCAMethod.getScaStatus());
        System.out.println("Acquired list of viable scaMethods -> SCA Method Selected");

        AuthoriseTransactionRequest authorisePayment = new AuthoriseTransactionRequest("123456",
                PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS,
                payment.getPaymentId(),
                scaStarted.getAuthorisationId());
        authorisePayment.addHeader("X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");
        SCA scaFinalized = standard.getCs().authoriseTransaction(authorisePayment);
        Assert.assertEquals(SCA.Status.FINALISED, scaFinalized.getScaStatus());
        System.out.println("Solved OTP Challenge -> Payment is finalized");
    }

    @Test
    @Tag("integration")
    public void authoriseConsentTransactionSparkasse() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(SPARKASSE_BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        Consent consent = this.getSparkasseConsent(standard.getCs());

        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(), consent.getId());
        request.getPsu().setId(SPARKASSE_PSU_ID);

        if (consent.getSca().getLinks().getUrlMapping().containsKey(LinkType.AUTHORISATION_WITH_PSU_AUTHENTICATION)) {
            PSUData psuData = new PSUData();
            psuData.setPassword(SPARKASSE_PIN);
            request.setPsuData(psuData);
        }

        SCA sca = standard.getCs().startAuthorisation(request);
        SCA scaFinalized = standard.getCs().authoriseTransaction(new AuthoriseTransactionRequest("111111", consent.getId(), sca.getAuthorisationId()));
        Assert.assertEquals(SCA.Status.FINALISED, scaFinalized.getScaStatus());
    }
}
