package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ConsentCreateAuthResourceRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ConsentUpdatePSUDataRequest;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TargobankTest {

    private static final String URL = "https://www.sandbox-bvxs2a.de/targobank/";
    private static final String BANK_VERLAG_TOKEN = "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE";

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE70300209005320320678"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE70300209005320320678"));

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
        createConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        consent = standard.getCs().createConsent(createConsentRequest);

        SCAApproach approach = SCAHandler.decision(consent);
        Assert.assertNotNull(consent.getId());
    }

    @Test
    @Tag("integration")
    public void getConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        GetConsentRequest getConsentRequest = new GetConsentRequest();
        getConsentRequest.setConsentId("rT2emZQ8mxH2VBPApZosBV9TiUsweAzkL0zFIPVCIVBc2-Pgi7MEMMIVlGwzdp3-AwsTYAZkvKgYQwZavZ1pB_SdMWF3876hAweK_n7HJlg=_=_psGLvQpt9Q");
        getConsentRequest.getHeaders().put("X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        Consent consent = standard.getCs().getConsent(getConsentRequest);
    }

    @Test
    @Tag("integration")
    public void getConsentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId("983fa01f-eedb-467d-849b-e81e1c8bf47a");

        assertThrows(BankRequestFailedException.class, () -> {
            standard.getCs().getStatus(statusConsentRequest);
        });
    }

    @Test
    @Tag("integration")
    public void deleteConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

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
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

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
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);

        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with transactions")
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

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
        consent.setValidUntil(new Date());
        // Build request body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        consent = standard.getCs().createConsent(createConsentRequest);

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

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
            Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    public void createNoPsuConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

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

        try {
            consent = standard.getCs().createConsent(createConsentRequest);
            Assert.fail("BankRequestFailedException exception not thrown.");  // This line should never be reached.
        } catch (BankRequestFailedException e) {
            Assert.assertEquals(400, e.getHttpStatusCode());
        }
    }

    @Test
    @Tag("integration")
    public void authoriseConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        ConsentCreateAuthResourceRequest consentCreateAuthResourceRequest =
                new ConsentCreateAuthResourceRequest("ioW1KUuefUzI_1LlM3qUd-6MzKMzjcaWwGTTG4GnPhrZB5wrJwUOjrROTKut-ViLQO3mGabdmvlZWBT56CplvvSdMWF3876hAweK_n7HJlg=_=_psGLvQpt9Q");
        consentCreateAuthResourceRequest.setPsu(psu);
        consentCreateAuthResourceRequest.getHeaders().put("X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        SCA sca = standard.getCs().startAuthorisationProcess(consentCreateAuthResourceRequest);
    }

    @Test
    @Tag("integration")
    public void updatePSUDataConsent() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS(URL, new BerlinGroupSigner()));

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        ConsentUpdatePSUDataRequest consentUpdatePSUDataRequest =
                new ConsentUpdatePSUDataRequest(
                        "Ub8XkbUGJVmbESyjFZqZzoj_PluPwrbZJiUTjTXlJCOF16E1zu1iJRNNZPUliEgLHQfChL30WsEvET_RBu9FfPSdMWF3876hAweK_n7HJlg=_=_psGLvQpt9Q",
                        "03f88668-06a3-406b-af1c-436979ad04cf");
        consentUpdatePSUDataRequest.setPsu(psu);
        consentUpdatePSUDataRequest.getHeaders().put("X-bvpsd2-test-apikey", "tUfZ5KOHRTFrikZUsmSMUabKw09UIzGE");

        standard.getCs().updatePSUData(consentUpdatePSUDataRequest);
    }
}
