package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
public class ConsentTest {
    private static final String BIC_SPARKASSE = "BYLADEM1FSI";
    private static final String BIC_FIDUCIA = "GENODEF1M03";

    @Test
    @Tag("integration")
    public void createConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_SPARKASSE, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE86999999990000001000", AccountReference.Type.IBAN));

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference("DE86999999990000001000", AccountReference.Type.IBAN));

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

        consent = standard.getCs().createConsent(createConsentRequest);

        SCAApproach approach = SCAHandler.decision(consent);
        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_SPARKASSE, true);

        Assert.assertTrue(standard.isCSImplemented());

        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setConsentId("b7c58287-5612-4b43-b837-8b2800917186");

        Consent consent = standard.getCs().getConsent(getConsentRequest);
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_SPARKASSE, true);

        Assert.assertTrue(standard.isCSImplemented());

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId("b7c58287-5612-4b43-b837-8b2800917186");

        assertThrows(BankRequestFailedException.class, () -> {
            standard.getCs().getStatus(statusConsentRequest);
        });
    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_SPARKASSE, true);

        Assert.assertTrue(standard.isCSImplemented());

        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
        deleteConsentRequest.setConsentId("b7c58287-5612-4b43-b837-8b2800917186");

        assertThrows(BankRequestFailedException.class, () -> {
            standard.getCs().deleteConsent(deleteConsentRequest);
        });
    }

    @Test
    @DisplayName("Create consent just with balances")
    @Tag("integration")
    public void createOnlyBalancesConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_FIDUCIA, true);
        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE40100100103307118608", AccountReference.Type.IBAN));

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with transactions")
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_FIDUCIA, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference("DE40100100103307118608", AccountReference.Type.IBAN));

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_FIDUCIA, true);
        Assert.assertTrue(standard.isCSImplemented());

        PSU psu = new PSU("4321-87654321-4321");
        Consent consent = new Consent();
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
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

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    public void createNoPsuConsent() throws BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC_FIDUCIA, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE40100100103307118608", AccountReference.Type.IBAN));

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference("DE40100100103307118608", AccountReference.Type.IBAN));

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

        try {
            consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            //TODO fix this test
            //Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }

}
