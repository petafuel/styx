package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.security.SignatureException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ConsentTest {

    @Test
    @Tag("integration")
    public void createConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));
        balances.add(new Account("DE02100100109307118603"));
        balances.add(new Account("DE67100100101306118605"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));


        PSU psu = new PSU("4321-87654321-4321");

        Consent consent = standard.getCs().createConsent(psu, balances, transactions, UUID.randomUUID());

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with balances")
    @Tag("integration")
    public void createOnlyBalancesConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        List<Account> transactions = null;

        PSU psu = new PSU("4321-87654321-4321");

        Consent consent = standard.getCs().createConsent(psu, balances, transactions, UUID.randomUUID());

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent just with transactions")
    @Tag("integration")
    public void createOnlyTransactionsConsent() throws SignatureException, BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = null;

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("4321-87654321-4321");

        Consent consent = standard.getCs().createConsent(psu, balances, transactions, UUID.randomUUID());

        Assert.assertNotNull(consent.getId());
    }

    @Test
    @DisplayName("Create consent without balances or transactions")
    @Tag("integration")
    public void createNoAccountsConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = null;

        List<Account> transactions = null;

        PSU psu = new PSU("4321-87654321-4321");

        try {
            Consent consent = standard.getCs().createConsent(psu, balances, transactions, UUID.randomUUID());
            Assert.fail("BankRequestFailedException exception not thrown.");
        } catch (BankRequestFailedException e) {
            // ToDo: Check for return code 400 from ASPSP.
            Assert.assertTrue(true);
        }
    }

    @Test
    @DisplayName("Create consent without PSU")
    @Tag("integration")
    @Disabled
    public void createNoPsuConsent() throws SignatureException {
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-test.fiduciagad.de/xs2a"));

        Assert.assertTrue(standard.isCSImplemented());

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE40100100103307118608"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE40100100103307118608"));

        PSU psu = new PSU("");

        try {
            Consent consent = standard.getCs().createConsent(psu, balances, transactions, UUID.randomUUID());
            Assert.fail("BankRequestFailedException exception not thrown.");
        } catch (BankRequestFailedException e) {
            // ToDo: Check for return code 400 or similar from ASPSP.
            // ToDo: Seems that the ASPSP returns a consent although required PSU-ID is missing.
            Assert.assertTrue(true);
        }
    }
}
