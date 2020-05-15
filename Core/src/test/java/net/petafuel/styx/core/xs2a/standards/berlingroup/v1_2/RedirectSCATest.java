package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RedirectSCATest {
    private static final String BIC = "CSDBDE71";

    @Test
    @Tag("integration")
    public void startSCA() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        Assert.assertTrue(standard.isCSImplemented());

        List<AccountReference> balances = new LinkedList<>();
        balances.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));
        balances.add(new AccountReference("DE98701204008538752000", AccountReference.Type.IBAN));
        balances.add(new AccountReference("DE36760300800500234565", AccountReference.Type.IBAN));

        List<AccountReference> transactions = new LinkedList<>();
        transactions.add(new AccountReference("DE60760300800500123456", AccountReference.Type.IBAN));

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");

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
        SCAApproach redirectSCA = SCAHandler.decision(consent);
        if (redirectSCA instanceof Redirect) {
            Assert.assertNotNull(((Redirect) redirectSCA).getAuthoriseLink());
        }
        //TODO call sca link
        //return redirect link to client
    }
}
