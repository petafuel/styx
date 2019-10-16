package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AISTest {
    private static final String FIDUCIA_GAD_BASE_API = "https://xs2a-test.fiduciagad.de/xs2a";
    private static final String DEUTSCHE_BANK_BASE_API = "https://simulator-xs2a.db.com/";

    private static final String CONSENT_ID = "3869582906101910204***REMOVED***CO4960JJ";
    private static final String ACCOUNT_ID = "3dc3d5b3-7023-4848-9853-f5400a64e80f";

    @Test
    @Tag("integration")
    public void testAccountList() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));
        ReadAccountListRequest r1 = new ReadAccountListRequest(CONSENT_ID);
        r1.setWithBalance(true);
        List<Account> list = standard.getAis().getAccountList(r1);
    }

    @Test
    @Tag("integration")
    public void testAccountDetails() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));
        ReadAccountDetailsRequest r1 = new ReadAccountDetailsRequest(ACCOUNT_ID, CONSENT_ID);
        r1.setWithBalance(true);

        Account result = standard.getAis().getAccount(r1);
    }

    @Test
    @Tag("integration")
    public void testBalances() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));

        ReadBalancesRequest r1 = new ReadBalancesRequest(ACCOUNT_ID, CONSENT_ID);

        Object result = standard.getAis().getBalancesByAccount(r1);
    }

    @Test
    @Tag("integration")
    public void testTransactions() {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(DEUTSCHE_BANK_BASE_API, new BerlinGroupSigner()));

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date from = format.parse("2019-03-03");
            Date to = new Date();
            ReadTransactionsRequest r1 = new ReadTransactionsRequest(ACCOUNT_ID, CONSENT_ID, "booked", from, to);
            Object result = standard.getAis().getTransactionsByAccount(r1);
        } catch (Exception ignored) {
        }
    }

    @Test
    @Tag("integration")
    public void testTransactionDetails() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS("https://xs2a.banking.co.at/xs2a-sandbox/m002", new BerlinGroupSigner()));

        ReadTransactionDetailsRequest r1 = new ReadTransactionDetailsRequest(ACCOUNT_ID, "3603140611280910256***REMOVED***CO4960JJ", CONSENT_ID);
        Object result = standard.getAis().getTransaction(r1);
    }
}
