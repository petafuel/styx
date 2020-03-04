package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ConsentUpdatePSUDataRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TargobankTest {

    private static final String BIC = "CMCIDEDD";
    private static final String BANK_VERLAG_TOKEN = "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE";
    private static final String CONSENT = "XorXzzKrmqFvKwtIVnnqp0fZ3Fyl0yCuIyQ96OEzojyAc9LfEVKhs-qhumi8p66h97Zw7L0UcJLvR3uke5rcjPSdMWF3876hAweK_n7HJlg=_=_psGLvQpt9Q";

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        Account account = new Account("DE70300209005320320678");
        account.setIdentifier("DE70300209005320320678");
        balances.add(account);

        List<Account> transactions = new LinkedList<>();
        transactions.add(account);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
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
        createConsentRequest.setTppRedirectPreferred(false);
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        consent = standard.getCs().createConsent(createConsentRequest);

        SCAApproach approach = SCAHandler.decision(consent);
        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());
        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setPsu(psu);
        getConsentRequest.setConsentId(CONSENT);

        getConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        Consent consent = standard.getCs().getConsent(getConsentRequest);
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());
        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId(CONSENT);
        statusConsentRequest.setPsu(psu);
        statusConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        Consent.State status = standard.getCs().getStatus(statusConsentRequest);
        Assert.assertTrue(status.equals(Consent.State.VALID) || status.equals(Consent.State.RECEIVED));

    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());
        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
        deleteConsentRequest.setPsu(psu);
        deleteConsentRequest.setConsentId(CONSENT);
        deleteConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        Consent c = standard.getCs().deleteConsent(deleteConsentRequest);
        Assert.assertEquals(Consent.State.TERMINATED_BY_TPP, c.getState());
    }

    @Test
    @Tag("integration")
    public void createOnlyBalancesConsent() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        Account account = new Account("DE40100100103307118608");
        account.setIdentifier("DE40100100103307118608");
        balances.add(account);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> transactions = new LinkedList<>();
        Account account = new Account("DE40100100103307118608");
        account.setIdentifier("DE40100100103307118608");
        transactions.add(account);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        Consent consent = new Consent();
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        Consent consent = new Consent();
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);
        Assertions.assertThrows(BankRequestFailedException.class, () -> standard.getCs().createConsent(createConsentRequest));
    }

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    public void createNoPsuConsent() throws SignatureException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);
        Assertions.assertThrows(BankRequestFailedException.class, () -> standard.getCs().createConsent(createConsentRequest));
    }

    @Test
    @Tag("integration")
    public void updatePSUDataConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        ConsentUpdatePSUDataRequest consentUpdatePSUDataRequest =
                new ConsentUpdatePSUDataRequest(
                        CONSENT,
                        "03f88668-06a3-406b-af1c-436979ad04cf");
        consentUpdatePSUDataRequest.setPsu(psu);
        consentUpdatePSUDataRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        Assertions.assertAll(() -> {
            standard.getCs().updatePSUData(consentUpdatePSUDataRequest);
        });
    }

    @Test
    @Tag("integration")
    public void initiateJsonPayment() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

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
        request.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        SCAApproach approach = SCAHandler.decision(payment);
        Assert.assertNotNull(payment);
    }

    @Test
    @Tag("integration")
    public void initiateJsonBulkPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        /** Debtor information*/
        String debtorIban = "DE40100100103307118608";
        Currency debtorCurrency = Currency.EUR;
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);

        /** Payment 1 information*/
        String creditorIban1 = "DE75999999990000001004";
        Currency creditorCurrency1 = Currency.EUR;
        String creditorName1 = "Creditor One";
        String instructedAmount1 = "0.99";
        Currency instructedCurrency1 = Currency.EUR;
        String reference1 = "Beispiel Verwendungszweck 1";

        Account creditor1 = new Account(creditorIban1, creditorCurrency1, Account.Type.IBAN);
        creditor1.setName(creditorName1);

        Payment p1 = new Payment();

        p1.setDebtor(debtor);
        p1.setCreditor(creditor1);
        p1.setInstructedAmount(new InstructedAmount(instructedAmount1, instructedCurrency1));
        p1.setRemittanceInformationUnstructured(reference1);
        p1.setEndToEndIdentification("RI-234567890");
        p1.setCreditorName(creditorName1);

        /** Payment 2 information*/
        String creditorIban2 = "DE75999999990000001004";
        Currency creditorCurrency2 = Currency.EUR;
        String creditorName2 = "Creditor Two";
        String instructedAmount2 = "1.50";
        Currency instructedCurrency2 = Currency.EUR;
        String reference2 = "Beispiel Verwendungszweck 2";

        Account creditor2 = new Account(creditorIban2, creditorCurrency2, Account.Type.IBAN);
        creditor2.setName(creditorName2);

        Payment p2 = new Payment();

        p2.setDebtor(debtor);
        p2.setCreditor(creditor2);
        p2.setCreditorName(creditorName2);
        p2.setInstructedAmount(new InstructedAmount(instructedAmount2, instructedCurrency2));
        p2.setRemittanceInformationUnstructured(reference2);
        p2.setEndToEndIdentification("WBG-123456789");

        List<Payment> payments = new LinkedList<>();
        payments.add(p1);
        payments.add(p2);

        BulkPayment bulkPayment = new BulkPayment();
        bulkPayment.setPayments(payments);
        bulkPayment.setDebtorAccount(debtor);
        bulkPayment.setBatchBookingPreferred(false);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        BulkPaymentInitiationJsonRequest request = new BulkPaymentInitiationJsonRequest(
                PaymentProduct.SEPA_CREDIT_TRANSFERS, bulkPayment, psu);
        request.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);

        InitiatedPayment initiatedPayment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(initiatedPayment);
    }
}
