package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.Redirect;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.sca.SCAMethod;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupCS;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupSigner;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RedirectSCATest {

    @Test
    @Tag("integration")
    public void startSCA() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        //standard.setCs(new BerlinGroupCS("https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://xs2a.banking.co.at/xs2a-sandbox/m002", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://simulator-xs2a.db.com/ais/sb/sandbox", new BerlinGroupSigner()));
        standard.setCs(new BerlinGroupCS("https://xs2a-sndbx.consorsbank.de", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://xs2a-sndbx.dab-bank.de", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://sandbox.sparda.de.schulung.sparda.de", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));
        balances.add(new Account("DE02100100109307118603"));
        balances.add(new Account("DE67100100101306118605"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        psu.setIdType("DE_ONLB_DB");

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

        consent = standard.getCs().createConsent(createConsentRequest);
        SCAMethod redirectSCA = SCAHandler.decision(consent);
        if(redirectSCA instanceof Redirect)
        {
            Assert.assertNotNull(((Redirect) redirectSCA).getRedirectLink());
        }
        //TODO call sca link
        //return redirect link to client
    }
}
