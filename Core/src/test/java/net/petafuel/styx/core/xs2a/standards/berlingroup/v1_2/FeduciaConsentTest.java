package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
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

import java.util.LinkedList;
import java.util.List;

@Tag("integration")
public class FeduciaConsentTest {

    private static final String BIC = "GENODEF1M03";
    private static final String CONSENT = "8612563016110020244***REMOVED***CO4960JJ";

    @Test
    @Tag("integration")
    public void createConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));
        balances.add(new Account("DE02100100109307118603"));
        balances.add(new Account("DE67100100101306118605"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));


        PSU psu = new PSU("4321-87654321-4321");

        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);

        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setConsentId(CONSENT);

        Consent consent = standard.getCs().getConsent(getConsentRequest);
        Assert.assertEquals(CONSENT, consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId(CONSENT);
        Consent.State state = standard.getCs().getStatus(statusConsentRequest);
        Assert.assertTrue(Consent.State.RECEIVED.equals(state) || Consent.State.VALID.equals(state));
    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
        deleteConsentRequest.setConsentId(CONSENT);

        Consent terminatedByTPP = standard.getCs().deleteConsent(deleteConsentRequest);
        Assert.assertEquals(Consent.State.TERMINATED_BY_TPP, terminatedByTPP.getState());
    }

    @Test
    @DisplayName("Create consent just with balances")
    @Tag("integration")
    public void createOnlyBalancesConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with transactions")
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        try {
            consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            //TODO fix this test
            //Assert.assertEquals(Integer.parseInt(String.valueOf(400)), e.getHttpStatusCode());
        }
    }

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    public void createNoPsuConsent() throws BankLookupFailedException, BankNotFoundException {
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
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        try {
            consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            //TODO fix this test
            //Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }

}
