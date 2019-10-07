package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.Redirect;
import net.petafuel.styx.core.xs2a.sca.SCAMethod;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RedirectSCATest {

    @Test
    public void startSCA() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-sndbx.consorsbank.de", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://sandbox.sparda.de.schulung.sparda.de", new BerlinGroupSigner()));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        //balances.add(new Account("DE40100100103307118608"));

        List<Account> transactions = new LinkedList<>();
        //transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("555-5555555");
        psu.setIpAddress("192.168.8.78");
        psu.setIdType("DE_ONLB_SB");

        //build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.getAccess().setBalances(balances);
        createConsentRequest.getAccess().setTransactions(transactions);
        createConsentRequest.setPsu(psu);
        createConsentRequest.setCombinedServiceIndicator(false);
        createConsentRequest.setRecurringIndicator(true);
        createConsentRequest.setFrequencyPerDay(4);
        createConsentRequest.setValidUntil(new Date());
        createConsentRequest.setTppRedirectPreferred(true);

        Consent consent = standard.getCs().createConsent(createConsentRequest);
        SCAMethod redirectSCA = SCAHandler.decision(consent);
        if(redirectSCA instanceof Redirect)
        {
            Assert.assertNotNull(((Redirect) redirectSCA).getRedirectLink());
        }
        //TODO call sca link
        //return redirect link to client
    }
}
