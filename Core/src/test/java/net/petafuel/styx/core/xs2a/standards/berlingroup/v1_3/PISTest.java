package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
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
    private static final String PAYMENT_ID = "d948e782-e235-4055-977d-95990ae894e9";

    @Test
    @Tag("integration")
    public void getPaymentStatus() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(SPARKASSE_BASE_API, new BerlinGroupSigner()));

        ReadPaymentStatusRequest r1 = new ReadPaymentStatusRequest(
                BerlinGroupPIS.PaymentService.PAYMENTS,
                BerlinGroupPIS.PaymentProduct.SEPA_CREDIT_TRANSFERS,
                PAYMENT_ID);

        PaymentStatus status = standard.getPis().getPaymentStatus(r1);
        Assert.assertEquals(Transaction.Status.RCVD, status.getTransactionStatus());
    }
}
