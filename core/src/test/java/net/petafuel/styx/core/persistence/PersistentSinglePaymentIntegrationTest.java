package net.petafuel.styx.core.persistence;


import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.persistence.models.PaymentEntry;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersistentSinglePaymentIntegrationTest {
    private PaymentEntry paymentEntry;
    private String paymentId;
    private String clientToken;
    private String bic;

    @BeforeAll
    public void prepare() {
        paymentEntry = new PaymentEntry();
        paymentId = UUID.randomUUID().toString();
        //TODO create accesstoken beforehand
        clientToken = Config.getInstance().getProperties().getProperty("test.token.access.pis");
        if (clientToken == null) {
            Assertions.fail("test.token.client.pis has to be set to a valid access token hash in the test resource for Core");
        }
        bic = "1231233D";
    }

    @Test
    @Order(1)
    void createPayment() {

        paymentEntry = PersistentPayment.create(paymentId,
                clientToken,
                bic,
                TransactionStatus.RCVD);
        Assert.assertEquals(TransactionStatus.RCVD, paymentEntry.getStatus());
        Assert.assertEquals(bic, paymentEntry.getBic());
        Assert.assertEquals(clientToken, paymentEntry.getClientToken().getId());
        Assert.assertEquals(paymentId, paymentEntry.getId());


    }

    @Test
    @Order(2)
    void getPayment() {
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.get(paymentEntry.getId());
        Assert.assertEquals(paymentEntry.getStatus(), paymentEntryFromDatabase.getStatus());
        Assert.assertEquals(paymentEntry.getBic(), paymentEntryFromDatabase.getBic());
        Assert.assertEquals(paymentEntry.getClientToken().getId(), paymentEntryFromDatabase.getClientToken().getId());
        Assert.assertEquals(paymentEntry.getId(), paymentEntryFromDatabase.getId());
    }

    @Test
    @Order(3)
    void updatePayment() {
        paymentEntry.setId(paymentId);
        paymentEntry.setStatus(TransactionStatus.ACCC);
        paymentEntry.setBic("DDDEEE123");
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.update(paymentEntry.getId(), paymentEntry.getClientToken().getId(), paymentEntry.getBic(), paymentEntry.getStatus());
        Assert.assertEquals(paymentEntry.getStatus(), paymentEntryFromDatabase.getStatus());
        Assert.assertEquals(paymentEntry.getBic(), paymentEntryFromDatabase.getBic());
        Assert.assertEquals(paymentEntry.getClientToken().getId(), paymentEntryFromDatabase.getClientToken().getId());
        Assert.assertEquals(paymentEntry.getId(), paymentEntryFromDatabase.getId());
    }

    @Test
    @Order(4)
    void updatePaymentStatus() {
        paymentEntry.setStatus(TransactionStatus.ACWP);
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.updateStatus(paymentEntry.getId(), paymentEntry.getStatus());
        Assert.assertEquals(paymentEntry.getStatus(), paymentEntryFromDatabase.getStatus());
        Assert.assertEquals(paymentEntry.getId(), paymentEntryFromDatabase.getId());
    }

    @Test
    @Order(5)
    void deletePayment() {
        PersistentPayment.delete(paymentEntry.getId());
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.get(paymentEntry.getId());
        Assert.assertNull(paymentEntryFromDatabase.getId());
        Assert.assertNull(paymentEntryFromDatabase.getBic());
        Assert.assertNull(paymentEntryFromDatabase.getClientToken());
        Assert.assertNull(paymentEntryFromDatabase.getStatus());
    }
}
