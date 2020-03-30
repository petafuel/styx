package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.Redirect;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ConsorsRedirectSCATest {
    private static final String BIC = "CSDBDE71";

    @Test
    @Tag("integration")
    public void startSCA() throws SignatureException, BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));

        List<AccountReference> accounts = new LinkedList<>();
        accounts.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));

        PSU psu = new PSU("555-5555555");
        psu.setIp("192.168.8.78");

        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.getAccess().setAccounts(accounts);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(true);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());

        //build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.setTppRedirectPreferred(true);

        consent = standard.getCs().createConsent(createConsentRequest);
        SCAApproach redirectSCA = SCAHandler.decision(consent);
        Assert.assertTrue(redirectSCA instanceof Redirect);
        Assert.assertNotNull(((Redirect) redirectSCA).getAuthoriseLink());
        String redirectLink = ((Redirect) redirectSCA).getAuthoriseLink().replace("\"", "") + "?psu-id=PSU-Successful";
        //TODO call sca link
        //return redirect link to client
    }
}
