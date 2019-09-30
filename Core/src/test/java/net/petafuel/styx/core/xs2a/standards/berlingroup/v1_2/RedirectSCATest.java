package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupCS;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.utils.SCAHandler;
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
        standard.setCs(new BerlinGroupCS("https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a", new BerlinGroupSigner()));
        //standard.setCs(new BerlinGroupCS("https://simulator-xs2a.db.com/ais/de/pfb", new BerlinGroupSigner()));


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
        createConsentRequest.setTppRedirectPreferred(false);

        Consent consent = standard.getCs().createConsent(createConsentRequest);
        SCAHandler.decision(consent);
        String url = "https://preprod-styx.paycenter.de:8444/v1/callbacks/" + consent.getConsentId();
        //TODO call sca link
        //return redirect link to client
    }
}
