package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationJsonRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;

public class ConsorsbankPISTest {

    private static final String CONSORSBANK = "https://xs2a-sndbx.consorsbank.de";

    @Test
    @Tag("integration")
    public void initiateJsonPeriodicPayment() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setPis(new BerlinGroupPIS(CONSORSBANK, new BerlinGroupSigner()));

        //payment information
        String creditorIban = "DE15500105172295759744"; //Consorsbank
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "WBG";
        String debtorIban = "DE60760300800500123456"; //Consorsbank
        Currency debtorCurrency = Currency.EUR;
        String amount = "520.00";
        Currency instructedCurrency = Currency.EUR;
        String reference = "Ref. Number WBG-1222";
        //additional periodic payment information
        Calendar day = Calendar.getInstance();
        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
        day.add(Calendar.MONTH, 1);
        Date startDate = day.getTime();
        day.add(Calendar.MONTH, 2);
        Date endDate = day.getTime();
        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.following;
        PeriodicPayment.Frequency frequency = PeriodicPayment.Frequency.MNTH;
        String dayOfExecution = "20";

        PeriodicPayment paymentBody = new PeriodicPayment(startDate, frequency);
        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
        creditor.setName(creditorName);
        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
        paymentBody.setCreditor(creditor);
        paymentBody.setDebtor(debtor);
        paymentBody.setAmount(amount);
        paymentBody.setCurrency(instructedCurrency);
        paymentBody.setReference(reference);
        paymentBody.setExecutionRule(executionRule);
        paymentBody.setEndDate(endDate);
        paymentBody.setDayOfExecution(dayOfExecution);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        PeriodicPaymentInitiationJsonRequest request = new PeriodicPaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(InitiatedPayment.Status.RCVD, payment.getStatus());
    }
}
