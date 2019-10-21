package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

public class PISTest {

    private static final String SPARKASSE_BASE_API = "https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678";
    private static final String PAYMENT_ID = "b23e90fa-dbfa-4379-a8ec-11e3910c570d";
    private static final String FIDUCIA_GAD_BASE_API = "https://xs2a-test.fiduciagad.de/xs2a";
    public static final String FIDUCIA_PAYMENT_ID = "3631391318101910234***REMOVED***PA4960JJ";

    @Test
    @Tag("integration")
    public void getPaymentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        ReadPaymentStatusRequest r1 = new ReadPaymentStatusRequest(
                PaymentService.PAYMENTS,
                PaymentProduct.SEPA_CREDIT_TRANSFERS,
                PAYMENT_ID);

        PaymentStatus status = standard.getPis().getPaymentStatus(r1);
        Assert.assertEquals(Transaction.Status.RCVD, status.getTransactionStatus());
    }

    @Test
    @Tag("integration")
    public void getPaymentStatusXML() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));

        ReadPaymentStatusRequest r1 = new ReadPaymentStatusRequest(
                PaymentService.PAYMENTS,
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS,
                FIDUCIA_PAYMENT_ID);

        PaymentStatus status = standard.getPis().getPaymentStatus(r1);
        Assert.assertEquals(Transaction.Status.RCVD, status.getTransactionStatus());
    }
}
