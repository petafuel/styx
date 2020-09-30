package net.petafuel.styx.core.persistence;

import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.ConsentStatus;
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
class PersistentConsentIntegrationTest {
    private Consent consent;

    @BeforeAll
    public void setup() {
        AccountReference IngoZebadich = new AccountReference("DE48500105171271124579", AccountReference.Type.IBAN);

        AccountReference MiaDochegal = new AccountReference("4253614013", AccountReference.Type.BBAN);

        AccountReference KlaraFall = new AccountReference("AAAPL1234C", AccountReference.Type.PAN);

        AccountReference JohannesBeer = new AccountReference("460323255272", AccountReference.Type.MSISDN);

        consent = new Consent();
        consent.setId(String.valueOf(UUID.randomUUID()));
        consent.setState(ConsentStatus.RECEIVED);
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
    void testDatabaseConnection() throws SQLException {
        Connection connection = Persistence.getInstance().getConnection();
        Assert.assertTrue(connection.isValid(1));
    }

    @Test
    @Order(2)
    void saveConsents() {
        Consent fromDatabase = new PersistentConsent().create(consent);
        Assert.assertNotNull(fromDatabase.getId());
        Assert.assertEquals(consent.getId(), fromDatabase.getId());
        Assert.assertEquals(ConsentStatus.RECEIVED, fromDatabase.getState());
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
    void getConsent() {
        Consent fromDatabase = new PersistentConsent().get(consent);
        Assert.assertNotNull(fromDatabase.getId());
        Assert.assertEquals(ConsentStatus.RECEIVED, fromDatabase.getState());
        Assert.assertEquals(SCA.Approach.REDIRECT, fromDatabase.getSca().getApproach());
        Assert.assertEquals("PSU-ID-33241", fromDatabase.getPsu().getId());
    }

    @Test
    @Order(4)
    void updateConsent() {
        consent.setCombinedServiceIndicator(true);
        consent.setFrequencyPerDay(1);
        consent.getSca().setApproach(SCA.Approach.DECOUPLED);
        consent.setState(ConsentStatus.VALID);

        Consent updatedConsent = new PersistentConsent().update(consent);
        Assert.assertNotNull(updatedConsent.getId());
        Assert.assertEquals(consent.getId(), updatedConsent.getId());
        Assert.assertEquals(ConsentStatus.VALID, updatedConsent.getState());
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
    void updateConsentState() {
        Consent updatedConsent = new PersistentConsent().updateState(consent, ConsentStatus.TERMINATED_BY_TPP);
        Assert.assertEquals(consent.getId(), updatedConsent.getId());
        Assert.assertEquals(ConsentStatus.TERMINATED_BY_TPP, updatedConsent.getState());
    }

    @Test
    @Order(6)
    void deleteConsent() {
        Consent fromDatabase = new PersistentConsent().delete(consent);
        Assert.assertEquals(consent.getId(), fromDatabase.getId());
    }
}
