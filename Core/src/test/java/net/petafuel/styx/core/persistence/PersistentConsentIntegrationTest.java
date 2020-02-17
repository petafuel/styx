package net.petafuel.styx.core.persistence;

import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.SCA;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersistentConsentIntegrationTest {
    private Consent consent;

    @BeforeAll
    public void setup() {
        Account IngoZebadich = new Account("DE48500105171271124579");
        IngoZebadich.setName("Ingo Zebadich");
        IngoZebadich.setType(Account.Type.IBAN);

        Account MiaDochegal = new Account("4253614013");
        MiaDochegal.setName("Mia Dochegal");
        MiaDochegal.setType(Account.Type.BBAN);

        Account KlaraFall = new Account("AAAPL1234C");
        KlaraFall.setName("Klara Fall");
        KlaraFall.setType(Account.Type.PAN);

        Account JohannesBeer = new Account("460323255272");
        JohannesBeer.setName("Johannes Beer");
        JohannesBeer.setType(Account.Type.MSISDN);

        consent = new Consent();
        consent.setId(String.valueOf(UUID.randomUUID()));
        consent.setState(Consent.State.RECEIVED);
        consent.getAccess().setTransactions(Arrays.asList(KlaraFall, JohannesBeer));
        consent.getAccess().setBalances(Arrays.asList(IngoZebadich, MiaDochegal));
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.getSca().setApproach(SCA.Approach.REDIRECT);
        consent.setCombinedServiceIndicator(false);

        PSU psu = new PSU("PSU-ID-33241");
        psu.setIdType("PSU-ID-TYPE-33241");
        psu.setIp("127.0.0.1");
        psu.setPort(9999);
        psu.setCorporateIdType("PSU-CO-ID-TYPE-33242");
        psu.setCorporateId("PSU-CO-ID-33242");
        psu.setGeoLocation("48.3938:11.7331");
        psu.setUserAgent("VIMpay 1.2.3");
        consent.setPsu(psu);
        consent.setxRequestId(UUID.randomUUID());
    }


    @Test
    @Order(1)
    public void testDatabaseConnection() throws SQLException {
        Connection connection = Persistence.getInstance().getConnection();
        Assert.assertTrue(connection.isValid(1));
    }

    @Test
    @Order(2)
    public void saveConsents() {
        Consent fromDatabase = new PersistentConsent().create(consent);
        Assert.assertNotNull(fromDatabase.getId());
        Assert.assertEquals(consent.getId(), fromDatabase.getId());
        Assert.assertEquals(Consent.State.RECEIVED, fromDatabase.getState());
        Assert.assertEquals(SCA.Approach.REDIRECT, fromDatabase.getSca().getApproach());
        Assert.assertFalse(fromDatabase.isRecurringIndicator());
        Assert.assertEquals(4, fromDatabase.getFrequencyPerDay());
        Assert.assertFalse(fromDatabase.isCombinedServiceIndicator());
        Assert.assertEquals("PSU-ID-33241", fromDatabase.getPsu().getId());
        Assert.assertEquals("PSU-ID-TYPE-33241", fromDatabase.getPsu().getIdType());
        Assert.assertEquals("127.0.0.1", fromDatabase.getPsu().getIp());
        Assert.assertEquals(java.util.Optional.of(9999).get(), fromDatabase.getPsu().getPort());
        Assert.assertEquals("PSU-CO-ID-TYPE-33242", fromDatabase.getPsu().getCorporateIdType());
        Assert.assertEquals("PSU-CO-ID-33242", fromDatabase.getPsu().getCorporateId());
        Assert.assertEquals("48.3938:11.7331", fromDatabase.getPsu().getGeoLocation());
        Assert.assertEquals("VIMpay 1.2.3", fromDatabase.getPsu().getUserAgent());

    }

    @Test
    @Order(3)
    public void getConsent() {
        Consent fromDatabase = new PersistentConsent().get(consent);
        Assert.assertNotNull(fromDatabase.getId());
        Assert.assertEquals(Consent.State.RECEIVED, fromDatabase.getState());
        Assert.assertEquals(SCA.Approach.REDIRECT, fromDatabase.getSca().getApproach());
        Assert.assertEquals("PSU-ID-33241", fromDatabase.getPsu().getId());
    }

    @Test
    @Order(4)
    public void updateConsent() {
        consent.setCombinedServiceIndicator(true);
        consent.setFrequencyPerDay(1);
        consent.getSca().setApproach(SCA.Approach.DECOUPLED);
        consent.setState(Consent.State.VALID);

        Consent updatedConsent = new PersistentConsent().update(consent);
        Assert.assertNotNull(updatedConsent.getId());
        Assert.assertEquals(consent.getId(), updatedConsent.getId());
        Assert.assertEquals(Consent.State.VALID, updatedConsent.getState());
        Assert.assertEquals(SCA.Approach.DECOUPLED, updatedConsent.getSca().getApproach());
        Assert.assertFalse(updatedConsent.isRecurringIndicator());
        Assert.assertEquals(1, updatedConsent.getFrequencyPerDay());
        Assert.assertTrue(updatedConsent.isCombinedServiceIndicator());
        Assert.assertEquals("PSU-ID-33241", updatedConsent.getPsu().getId());
        Assert.assertEquals("PSU-ID-TYPE-33241", updatedConsent.getPsu().getIdType());
        Assert.assertEquals("127.0.0.1", updatedConsent.getPsu().getIp());
        Assert.assertEquals(java.util.Optional.of(9999).get(), updatedConsent.getPsu().getPort());
        Assert.assertEquals("PSU-CO-ID-TYPE-33242", updatedConsent.getPsu().getCorporateIdType());
        Assert.assertEquals("PSU-CO-ID-33242", updatedConsent.getPsu().getCorporateId());
        Assert.assertEquals("48.3938:11.7331", updatedConsent.getPsu().getGeoLocation());
        Assert.assertEquals("VIMpay 1.2.3", updatedConsent.getPsu().getUserAgent());
    }

    @Test
    @Order(5)
    public void updateConsentState() {
        Consent updatedConsent = new PersistentConsent().updateState(consent, Consent.State.TERMINATED_BY_TPP);
        Assert.assertEquals(consent.getId(), updatedConsent.getId());
        Assert.assertEquals(Consent.State.TERMINATED_BY_TPP, updatedConsent.getState());
    }

    @Test
    @Order(6)
    public void deleteConsent() {
        Consent fromDatabase = new PersistentConsent().delete(consent);
        Assert.assertEquals(consent.getId(), fromDatabase.getId());
    }
}
