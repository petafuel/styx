package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConsentTest {

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://simulator-xs2a.db.com/", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));
        balances.add(new Account("DE02100100109307118603"));
        balances.add(new Account("DE67100100101306118605"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));


        PSU psu = new PSU("4321-87654321-4321");

        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.getAccess().setBalances(balances);
        createConsentRequest.getAccess().setTransactions(transactions);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());
        //createConsentRequest.setTppRedirectPreferred(true);

        Consent consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        //standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));
        standard.setCs(new BerlinGroupCS("https://xs2a-sndbx.consorsbank.de", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setConsentId("EM1-vesW6HU5cDpPh_bpc65mJd0dlnpO8qmvGlgGViZNGGhgcbQrDieFkQ9dkzEmzEogXxwAl1YzF4bFRYCMAg==_=_bS6p6XvTWI");

        Consent consent = standard.getCs().getConsent(getConsentRequest);
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId("2337125702280910210***REMOVED***CO4960JJ");

        assertThrows(BankRequestFailedException.class, () -> {
            standard.getCs().getStatus(statusConsentRequest);
        });
    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
        deleteConsentRequest.setConsentId("sometest-BAFIN-125314CO4960JJ");

        assertThrows(BankRequestFailedException.class, () -> {
            standard.getCs().deleteConsent(deleteConsentRequest);
        });
    }

    @Test
    @DisplayName("Create consent just with balances")
    @Tag("integration")
    public void createOnlyBalancesConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("4321-87654321-4321");

        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.getAccess().setBalances(balances);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());

        Consent consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with transactions")
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("4321-87654321-4321");

        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.getAccess().setTransactions(transactions);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());

        Consent consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        PSU psu = new PSU("4321-87654321-4321");

        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());

        try {
            Consent consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    public void createNoPsuConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("");

        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.getAccess().setBalances(balances);
        createConsentRequest.getAccess().setTransactions(transactions);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());

        try {
            Consent consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }
  
}
