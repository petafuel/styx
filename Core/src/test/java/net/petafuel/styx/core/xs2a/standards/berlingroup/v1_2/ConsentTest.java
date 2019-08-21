package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ConsentTest {

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

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
        createConsentRequest.setBalances(balances);
        createConsentRequest.setTransactions(transactions);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(false);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());

        Consent consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertTrue(consent.getId() != null);
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setConsentId("4574072119180910242***REMOVED***CO4960JJ");

        Consent consent = standard.getCs().getConsent(getConsentRequest);
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId("4574072119180910242***REMOVED***CO4960JJ");

        //Consent consent = standard.getCs().getStatus(statusConsentRequest);
    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));
        standard.setAis(new BerlinGroupAIS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
        deleteConsentRequest.setConsentId("4574072119180910242***REMOVED***CO4960JJ");

        //Consent consent = standard.getCs().getStatus(deleteConsentRequest);
    }

    @Test
    public void test()
    {
        System.out.println("tesadsadadasdst");
    }
}
