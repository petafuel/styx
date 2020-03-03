package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AuthorisationsTest {

    private static final String SPARKASSE_BIC = "BYLADEM1FSI";
    private static final String SPARKASSE_PIN_VALID = "okok1";
    private static final String SPARKASSE_PSU_ID = "smsTAN_singleMed";
    private static final String SPARKASSE_PSU_IP_ADDRESS = "192.168.8.78";

    private static final String TARGO_PSU_ID = "PSD2TEST4";
    private static final String TARGO_PSU_IP_ADDRESS = "192.168.8.78";
    private static final String TARGO_BIC = "CMCIDEDD";
    private static final String TARGO_STATIC_API_TOKEN = "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE";

    @Test
    @Tag("integration")
    public void startAuthorisationSparkasse() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(SPARKASSE_BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        PSUData psuData = new PSUData();
        psuData.setPassword(SPARKASSE_PIN_VALID);

        Consent consent = this.getSparkasseConsent(standard.getCs());
        StartAuthorisationRequest request = new StartAuthorisationRequest(psuData,"consents/" + consent.getId());
        request.getPsu().setId("smsTAN_singleMed");

        SCA sca = standard.getCs().startAuthorisation(request);
    }

    @Test
    @Tag("integration")
    public void getCSAuthorisationAndSCAStatusSparkasse() throws BankRequestFailedException, BankNotFoundException, BankLookupFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(SPARKASSE_BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        PSUData psuData = new PSUData();
        psuData.setPassword(SPARKASSE_PIN_VALID);

        Consent consent = this.getSparkasseConsent(standard.getCs());
        StartAuthorisationRequest request = new StartAuthorisationRequest(psuData,"consents/" + consent.getId());
        request.getPsu().setId("smsTAN_singleMed");

        SCA sca = standard.getCs().startAuthorisation(request);

        GetAuthorisationRequest getAuthorisationRequest = new GetAuthorisationRequest(consent.getId());

        GetSCAStatusRequest getSCAStatusRequest = new GetSCAStatusRequest(consent.getId(), sca.getAuthorisationId());

        List<String> authIdList = standard.getCs().getAuthorisationRequest(getAuthorisationRequest);

        String scaStatus = standard.getCs().getSCAStatus(getSCAStatusRequest);

        Assert.assertEquals(scaStatus, sca.getStatus().getValue());
        Assert.assertTrue(authIdList.contains(sca.getAuthorisationId()));
    }

    @Test
    @Tag("integration")
    public void startAuthorisationTargo() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(),"payments/" +
                PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue() + "/" + payment.getPaymentId());
        request.getPsu().setId(TARGO_PSU_ID);

        Object obj = standard.getPis().startAuthorisation(request);
//        net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException: {"tppMessages":[{"category":"ERROR","code":"INTERNAL_SERVER_ERROR","text":"Internal Server Error"}]}
    }

    @Test
    @Tag("integration")
    public void getPISAuthorisationAndSCAStatusTargo() throws BankRequestFailedException, BankNotFoundException, BankLookupFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(), PaymentService.PAYMENTS.getValue() + "/" +
                PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue() + "/" + payment.getPaymentId());
        request.getPsu().setId(TARGO_PSU_ID);

        SCA sca = standard.getPis().startAuthorisation(request);

        GetAuthorisationRequest getAuthorisationRequest = new GetAuthorisationRequest(PaymentService.PAYMENTS.getValue(),
                PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue(), payment.getPaymentId());

        GetSCAStatusRequest getSCAStatusRequest = new GetSCAStatusRequest(PaymentService.PAYMENTS.getValue(),
                PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue(), payment.getPaymentId(), sca.getAuthorisationId());

        List<String> authIdList = standard.getPis().getAuthorisationRequest(getAuthorisationRequest);

        String scaStatus = standard.getPis().getSCAStatus(getSCAStatusRequest);

        Assert.assertEquals(scaStatus, sca.getStatus().getValue());
        Assert.assertTrue(authIdList.contains(sca.getAuthorisationId()));
    }

    @Test
    @Tag("integration")
    public void startAuthorisationConsentFiducia() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {

        XS2AStandard standard = (new SAD()).getBankByBIC(TARGO_BIC, true);

        Assert.assertTrue(standard.isPISImplemented());

        InitiatedPayment payment = this.getTargoPayment(standard.getPis());
        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(), "payments/" +
                PaymentProduct.SEPA_CREDIT_TRANSFERS.getValue() + "/" + payment.getPaymentId());
        request.getPsu().setId(TARGO_PSU_ID);

        Object obj = standard.getPis().startAuthorisation(request);
    }

    private Consent getSparkasseConsent(CSInterface csInterface) throws BankRequestFailedException {

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE86999999990000001000"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE86999999990000001000"));

        PSU psu = new PSU("smsTAN_singleMed");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.setTppRedirectPreferred(true);

        return csInterface.createConsent(createConsentRequest);
    }

    private Consent getTargoConsent(CSInterface csInterface) throws BankRequestFailedException {

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE86999999990000001000"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE86999999990000001000"));

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.setTppRedirectPreferred(true);

        return csInterface.createConsent(createConsentRequest);
    }

    private InitiatedPayment getTargoPayment(PISInterface pisInterface) throws BankRequestFailedException {
        //payment information
        String creditorIban = "DE75999999990000001004";
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE40100100103307118608";
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

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);
        request.getHeaders().put("X-bvpsd2-test-apikey", TARGO_STATIC_API_TOKEN);

        return pisInterface.initiatePayment(request);
    }

    @Test
    @Tag("integration")
    public void psuDataSerializer() {

        StartAuthorisationRequest request = new StartAuthorisationRequest(new PSUData(), "/v1/consents/12393021");
        request.getPsuData().setPassword("okok1");
        String result = null;

        System.out.println(request.getRawBody());
    }
}
