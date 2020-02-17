package net.petafuel.styx.core.persistence;


import net.petafuel.styx.core.persistence.layers.PersistentPayment;
import net.petafuel.styx.core.persistence.models.PaymentEntry;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import org.junit.Assert;
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
public class PersistentPaymentIntegrationTest {
    private PaymentEntry paymentEntry;
    private String paymentId;
    private UUID clientToken;
    private String bic;

    @BeforeAll
    public void prepare() {
        paymentEntry = new PaymentEntry();
        paymentId = UUID.randomUUID().toString();
        //TODO create accesstoken beforehand
        clientToken = UUID.fromString("d0b10916-7926-4b6c-a90c-3643c62e4b08");
        bic = "1231233D";
    }

    @Test
    @Order(1)
    public void createPayment() {

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
    public void getPayment() {
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.get(paymentEntry.getId());
        Assert.assertEquals(paymentEntry.getStatus(), paymentEntryFromDatabase.getStatus());
        Assert.assertEquals(paymentEntry.getBic(), paymentEntryFromDatabase.getBic());
        Assert.assertEquals(paymentEntry.getClientToken().getId(), paymentEntryFromDatabase.getClientToken().getId());
        Assert.assertEquals(paymentEntry.getId(), paymentEntryFromDatabase.getId());
    }

    @Test
    @Order(3)
    public void updatePayment() {
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
    public void updatePaymentStatus() {
        paymentEntry.setStatus(TransactionStatus.ACWP);
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.updateStatus(paymentEntry.getId(), paymentEntry.getStatus());
        Assert.assertEquals(paymentEntry.getStatus(), paymentEntryFromDatabase.getStatus());
        Assert.assertEquals(paymentEntry.getId(), paymentEntryFromDatabase.getId());
    }

    @Test
    @Order(5)
    public void deletePayment() {
        PersistentPayment.delete(paymentEntry.getId());
        PaymentEntry paymentEntryFromDatabase = PersistentPayment.get(paymentEntry.getId());
        Assert.assertNull(paymentEntryFromDatabase.getId());
        Assert.assertNull(paymentEntryFromDatabase.getBic());
        Assert.assertNull(paymentEntryFromDatabase.getClientToken());
        Assert.assertNull(paymentEntryFromDatabase.getStatus());
    }
}
