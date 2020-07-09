//package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;
//
//import net.petafuel.styx.core.banklookup.XS2AStandard;
//import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
//import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
//import net.petafuel.styx.core.banklookup.sad.SAD;
//import net.petafuel.styx.core.xs2a.entities.Account;
//import net.petafuel.styx.core.xs2a.entities.Currency;
//import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
//import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
//import net.petafuel.styx.core.xs2a.entities.PSU;
//import net.petafuel.styx.core.xs2a.entities.Payment;
//import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
//import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
//import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
//import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
//import org.junit.Assert;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//
//import java.text.ParseException;
//import java.util.Calendar;
//import java.util.Date;
//
//public class ConsorsbankPISTest {
//
//    private static final String BIC = "CSDBDE71";
//
//    @Test
//    @Tag("integration")
//    public void initiateJsonPeriodicPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException, ParseException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        //payment information
//        String creditorIban = "DE15500105172295759744"; //Consorsbank
//        Currency creditorCurrency = Currency.EUR;
//        String creditorName = "WBG";
//        String debtorIban = "DE60760300800500123456"; //Consorsbank
//        Currency debtorCurrency = Currency.EUR;
//        Currency instructedCurrency = Currency.EUR;
//        String reference = "Ref. Number WBG-1222";
//        //additional periodic payment information
//        Calendar day = Calendar.getInstance();
//        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
//        day.add(Calendar.MONTH, 1);
//        Date startDate = day.getTime();
//        day.add(Calendar.MONTH, 2);
//        Date endDate = day.getTime();
//        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.FOLLOWING;
//        String frequency = PeriodicPayment.Frequency.MNTH.name();
//        if (standard.getAspsp().getConfig().getImplementerOptions().get("STYX01").getOptions()
//                .get("required").getAsBoolean()) {
//            frequency = PeriodicPayment.Frequency.MNTH.getValue();
//        }
//        String dayOfExecution = "20";
//
//        PeriodicPayment paymentBody = new PeriodicPayment(startDate, frequency);
//        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
//        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
//        paymentBody.setCreditor(creditor);
//        paymentBody.setCreditorName(creditorName);
//        paymentBody.setDebtor(debtor);
//        InstructedAmount instructedAmount = new InstructedAmount("520.00");
//        instructedAmount.setCurrency(Currency.EUR);
//        paymentBody.setInstructedAmount(instructedAmount);
//        paymentBody.setRemittanceInformationUnstructured(reference);
//        paymentBody.setExecutionRule(executionRule);
//        paymentBody.setEndDate(endDate);
//        paymentBody.setDayOfExecution(dayOfExecution);
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//        PeriodicPaymentInitiationJsonRequest request = new PeriodicPaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
//        request.setTppRedirectPreferred(true);
//
//        InitiatedPayment payment = standard.getPis().initiatePayment(request);
//        Assert.assertNotNull(payment);
//        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
//    }
//
//    @Test
//    @Tag("integration")
//    public void initiateJsonPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException, ParseException {
//        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
//
//        //payment information
//        String creditorIban = "DE60760300800500123456"; //Consorsbank
//        Currency creditorCurrency = Currency.EUR;
//        String creditorName = "WBG";
//        String debtorIban = "DE23760300800550123451"; //Consorsbank
//        Currency debtorCurrency = Currency.EUR;
//        String amount = "520.00";
//        Currency instructedCurrency = Currency.EUR;
//        String reference = "Ref. Number WBG-1222";
//        //additional periodic payment information
//        Calendar day = Calendar.getInstance();
//        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
//        day.add(Calendar.MONTH, 1);
//        day.add(Calendar.MONTH, 2);
//        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.FOLLOWING;
//        String dayOfExecution = "20";
//
//        Payment paymentBody = new Payment();
//        Account creditor = new Account(creditorIban, creditorCurrency, Account.Type.IBAN);
//        Account debtor = new Account(debtorIban, debtorCurrency, Account.Type.IBAN);
//        paymentBody.setCreditor(creditor);
//        paymentBody.setDebtor(debtor);
//        paymentBody.setInstructedAmount(new InstructedAmount(amount, instructedCurrency));
//        paymentBody.setRemittanceInformationUnstructured(reference);
//        paymentBody.setCreditorName(creditorName);
//
//        PSU psu = new PSU("PSU-Successful");
//        psu.setIp("192.168.8.78");
//        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
//        request.setTppRedirectPreferred(true);
//
//        InitiatedPayment payment = standard.getPis().initiatePayment(request);
//        Assert.assertNotNull(payment);
//        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
//    }
//}
