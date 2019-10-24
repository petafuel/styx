package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.*;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

public class PISTest {

    private static final String SPARKASSE_BASE_API = "https://xs2a-sandbox.f-i-apim.de:8444/fixs2a-env/xs2a-api/12345678";
    private static final String PAYMENT_ID = "b23e90fa-dbfa-4379-a8ec-11e3910c570d";
    private static final String FIDUCIA_GAD_BASE_API = "https://xs2a-test.fiduciagad.de/xs2a";
    public static final String FIDUCIA_PAYMENT_ID = "3631391318101910234***REMOVED***PA4960JJ";
    public static final String DEUTSCHE_BANK_BASE_API ="https://simulator-xs2a.db.com:443/sb/sandbox";
    public static final String FIDOR_BANK_BASE_API = "https://xs2a.sandbox.fidorsolutions.cloud";

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

    @Test
    @Tag("integration")
    public void initiateJSONPayment() {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(FIDOR_BANK_BASE_API, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE75999999990000001004"; //Sparkasse
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE86999999990000001000"; //Sparkasse
        Currency debtorCurrency = Currency.EUR;
        String amount = "0.99";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Beispiel Verwendungszweck";

        Payment paymentBody = new Payment();
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        creditor.setName(creditorName);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setAmount(amount);
        paymentBody.setCurrency(instructedCurrency);
        paymentBody.setReference(reference);

        PSU psu = new PSU("PSU-1234");
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);

        try {
            InitiatedPayment payment = standard.getPis().initiatePayment(request);
            Assert.assertTrue(true);
        }
        catch (Exception e)
        {
            Assert.assertTrue(false);
        }
    }
}
