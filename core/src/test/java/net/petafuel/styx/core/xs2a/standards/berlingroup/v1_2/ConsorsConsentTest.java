//package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;
//
//import net.petafuel.styx.core.banklookup.XS2AStandard;
//import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
//import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
//import net.petafuel.styx.core.banklookup.sad.SAD;
//import net.petafuel.styx.core.xs2a.entities.AccountReference;
//import net.petafuel.styx.core.xs2a.entities.Consent;
//import net.petafuel.styx.core.xs2a.entities.PSU;
//import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
//import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
//import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
//import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
//import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
//import org.junit.Assert;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//
//@Tag("integration")
//public class ConsorsConsentTest {
//
//    private static final String BIC = "CSDBDE71";
//    private static final String CONSENT = "YTYcQTMAWsNhJL-iAJ5DRSvD4Wkq4-rI0vZuXPGnTrBLwxruB4iBC2rLdxe_JmLVmNUZrDfhkFiwk2pKoYyLcw==_=_bS6p6XvTWI";
//
//    @Test
//    @Tag("integration")
//    public void createConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//        Assert.assertTrue(standard.isCSImplemented());
//
//        List<AccountReference> balances = new LinkedList<>();
//        balances.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        List<AccountReference> transactions = new LinkedList<>();
//        transactions.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        List<AccountReference> accounts = new LinkedList<>();
//        accounts.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//
//        Consent consent = new Consent();
//        consent.getAccess().setBalances(balances);
//        consent.getAccess().setTransactions(transactions);
//        consent.getAccess().setAccounts(transactions);
//        consent.setPsu(psu);
//        consent.setCombinedServiceIndicator(false);
//        consent.setRecurringIndicator(true);
//        consent.setFrequencyPerDay(4);
//        consent.setValidUntil(new Date());
//
//        // build Request Body
//        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
//        createConsentRequest.setTppRedirectPreferred(true);
//
//        consent = standard.getCs().createConsent(createConsentRequest);
//
//        Assert.assertNotNull(consent.getId());
//    }
//
//    @Test
//    @Tag("integration")
//    public void getConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        GetConsentRequest getConsentRequest = new GetConsentRequest();
//        getConsentRequest.setConsentId(CONSENT);
//
//        Consent consent = standard.getCs().getConsent(getConsentRequest);
//        Assert.assertEquals(CONSENT, consent.getId());
//    }
//
//    @Test
//    @Tag("integration")
//    public void getConsentStatus() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
//        statusConsentRequest.setConsentId(CONSENT);
//        Consent.State state = standard.getCs().getStatus(statusConsentRequest);
//        Assert.assertTrue(Consent.State.RECEIVED.equals(state) || Consent.State.VALID.equals(state));
//    }
//
//    @Test
//    @Tag("integration")
//    public void deleteConsent() throws BankLookupFailedException, BankNotFoundException, BankRequestFailedException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        DeleteConsentRequest deleteConsentRequest = new DeleteConsentRequest();
//        deleteConsentRequest.setConsentId(CONSENT);
//
//        Consent terminatedByTPP = standard.getCs().deleteConsent(deleteConsentRequest);
//        Assert.assertEquals(Consent.State.TERMINATED_BY_TPP, terminatedByTPP.getState());
//    }
//
//    @Test
//    @DisplayName("Create consent just with balances")
//    @Tag("integration")
//    public void createOnlyBalancesConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        List<AccountReference> balances = new LinkedList<>();
//        balances.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//        Consent consent = new Consent();
//        consent.getAccess().setBalances(balances);
//        consent.setPsu(psu);
//        consent.setCombinedServiceIndicator(false);
//        consent.setRecurringIndicator(false);
//        consent.setFrequencyPerDay(4);
//        // Build request body
//        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
//
//        consent = standard.getCs().createConsent(createConsentRequest);
//
//        Assert.assertNotNull(consent.getId());
//    }
//
//    @Test
//    @DisplayName("Create consent just with transactions")
//    @Tag("integration")
//    public void createOnlyTransactionsConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        List<AccountReference> transactions = new LinkedList<>();
//        transactions.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//        Consent consent = new Consent();
//        consent.getAccess().setTransactions(transactions);
//        consent.setPsu(psu);
//        consent.setCombinedServiceIndicator(false);
//        consent.setRecurringIndicator(false);
//        consent.setFrequencyPerDay(4);
//        // Build request body
//        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
//
//        consent = standard.getCs().createConsent(createConsentRequest);
//
//        Assert.assertNotNull(consent.getId());
//    }
//
//    @Test
//    @DisplayName("Create consent without balances or transactions")
//    @Tag("integration")
//    public void createNoAccountsConsent() throws BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//        Consent consent = new Consent();
//        consent.setPsu(psu);
//        consent.setCombinedServiceIndicator(false);
//        consent.setRecurringIndicator(false);
//        consent.setFrequencyPerDay(4);
//        // Build request body
//        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
//
//        try {
//            consent = standard.getCs().createConsent(createConsentRequest);
//            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
//        } catch (BankRequestFailedException e) {
//            //TODO Fix this test
//            //Assert.assertEquals(400, e.getHttpStatusCode());
//        }
//    }
//
//    @Test
//    @DisplayName("Create consent without PSU")
//    @Tag("integration")
//    public void createNoPsuConsent() throws BankLookupFailedException, BankNotFoundException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        Assert.assertTrue(standard.isCSImplemented());
//
//        List<AccountReference> balances = new LinkedList<>();
//        balances.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        List<AccountReference> transactions = new LinkedList<>();
//        transactions.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
//
//        PSU psu = new PSU("");
//        Consent consent = new Consent();
//        consent.getAccess().setBalances(balances);
//        consent.getAccess().setTransactions(transactions);
//        consent.setPsu(psu);
//        consent.setCombinedServiceIndicator(false);
//        consent.setRecurringIndicator(false);
//        consent.setFrequencyPerDay(4);
//        // Build request body
//        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
//
//        try {
//            consent = standard.getCs().createConsent(createConsentRequest);
//            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
//        } catch (BankRequestFailedException e) {
//            //Fix this test
//            //Assert.assertEquals(400, e.getHttpStatusCode());
//        }
//    }
//
//}
