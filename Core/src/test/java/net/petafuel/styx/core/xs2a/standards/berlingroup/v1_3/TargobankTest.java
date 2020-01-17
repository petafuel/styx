package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
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
    private static final String CONSENT = "P6wMwv2MAlyqHLxiQ0W9Jao0rBZp90l7RoRUWHW5YG2tvuV4UiKKo5eMVlEsrA-VCSmy8mjGAmN707bED6NC__SdMWF3876hAweK_n7HJlg=_=_psGLvQpt9Q";

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

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
        balances.add(new Account("DE40100100103307118608"));

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
        transactions.add(new Account("DE40100100103307118608"));

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
    public void authoriseConsent() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        PSU psu = new PSU("PSD2TEST4");
        psu.setIp("255.255.255.0");
        ConsentCreateAuthResourceRequest consentCreateAuthResourceRequest =
                new ConsentCreateAuthResourceRequest(CONSENT);
        consentCreateAuthResourceRequest.setPsu(psu);
        consentCreateAuthResourceRequest.getHeaders().put("X-bvpsd2-test-apikey", BANK_VERLAG_TOKEN);
        SCA sca = standard.getCs().startAuthorisationProcess(consentCreateAuthResourceRequest);
        Assert.assertNotNull(sca);
        Assert.assertNotNull(sca.getAuthorisationId());
        Assert.assertNotNull(sca.getApproach());
        Assert.assertEquals(SCA.Status.PSUIDENTIFIED, sca.getStatus());
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

        standard.getCs().updatePSUData(consentUpdatePSUDataRequest);
    }
}
