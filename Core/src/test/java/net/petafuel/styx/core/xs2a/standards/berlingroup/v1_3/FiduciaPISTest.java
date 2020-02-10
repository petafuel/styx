package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.jsepa.model.CCTInitiation;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.GroupHeader;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationJsonRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationPain001Request;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.utils.jsepa.PmtInf;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class FiduciaPISTest {
    private static final String BIC = "GENODEF1M03";

    @Test
    @Tag("integration")
    public void initiateXMLPeriodicPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        //  PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        //  use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
//        PaymentInstructionInformation pii = new PaymentInstructionInformation();
        PmtInf pii = new PmtInf();
        CreditTransferTransactionInformation cdtTrfTxInf1 = new CreditTransferTransactionInformation();

        //build xml request part
        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-11-11";
        int numberOfTransactions = 2;
        double controlSum = 200.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-11-11";
        String debtorName = "Debtor Name";
        String debtorIban = "DE60760300800500123456";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        double amount1 = 100.00;
        String endToEndID = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98701204008538752000";
        String purpose1 = "purpose string";
        String creditorAgent1 = "AGENT1";

        //payment information
        //additional periodic payment information
        Calendar day = Calendar.getInstance();
        day.set(Calendar.DAY_OF_MONTH, day.getActualMinimum(Calendar.DAY_OF_MONTH));
        day.add(Calendar.MONTH, 1);
        Date startDate = day.getTime();
        day.add(Calendar.MONTH, 2);
        Date endDate = day.getTime();
        PeriodicPayment.ExecutionRule executionRule = PeriodicPayment.ExecutionRule.following;
        PeriodicPayment.Frequency frequency = PeriodicPayment.Frequency.WEEK;
        String dayOfExecution = "20";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf1.setEndToEndID(endToEndID);
        cdtTrfTxInf1.setAmount(amount1);
        cdtTrfTxInf1.setCreditorName(creditorName1);
        cdtTrfTxInf1.setCreditorIBAN(creditorIBAN1);
        cdtTrfTxInf1.setVwz(purpose1);
        cdtTrfTxInf1.setCreditorAgent(creditorAgent1);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        list.add(cdtTrfTxInf1);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(numberOfTransactions);
        pii.setCtrlSum(controlSum);
        pii.setRequestedExecutionDate(requestedExecutionDate);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(debtorIban);
        pii.setDebitorBic(debtorBic);
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(batchBooking);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        PaymentInitiationPain001Request xmlRequest = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.PERIODIC_PAYMENTS, document, psu
        );
        xmlRequest.setTppRedirectPreferred(true);

        //build json request part
        PeriodicPayment paymentBody = new PeriodicPayment(startDate, frequency.getName());
        paymentBody.setExecutionRule(executionRule);
        paymentBody.setEndDate(endDate);
        paymentBody.setDayOfExecution(dayOfExecution);

        //merge together xml and json into one request
        PeriodicPaymentInitiationXMLRequest request = new PeriodicPaymentInitiationXMLRequest(xmlRequest, paymentBody);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
    }

    @Test
    @Tag("integration")
    public void initiateXMLPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        //  PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        //  use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
//        PaymentInstructionInformation pii = new PaymentInstructionInformation();
        PmtInf pii = new PmtInf();
        CreditTransferTransactionInformation cdtTrfTxInf1 = new CreditTransferTransactionInformation();

        //build xml request part
        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-11-11";
        int numberOfTransactions = 2;
        double controlSum = 200.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-11-11";
        String debtorName = "Debtor Name";
        String debtorIban = "DE60760300800500123456";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        double amount1 = 100.00;
        String endToEndID = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98701204008538752000";
        String purpose1 = "purpose string";
        String creditorAgent1 = "AGENT1";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf1.setEndToEndID(endToEndID);
        cdtTrfTxInf1.setAmount(amount1);
        cdtTrfTxInf1.setCreditorName(creditorName1);
        cdtTrfTxInf1.setCreditorIBAN(creditorIBAN1);
        cdtTrfTxInf1.setVwz(purpose1);
        cdtTrfTxInf1.setCreditorAgent(creditorAgent1);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        list.add(cdtTrfTxInf1);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(numberOfTransactions);
        pii.setCtrlSum(controlSum);
        pii.setRequestedExecutionDate(requestedExecutionDate);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(debtorIban);
        pii.setDebitorBic(debtorBic);
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(batchBooking);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        PaymentInitiationPain001Request request = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.PAYMENTS, document, psu
        );
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
        Assert.assertEquals(TransactionStatus.RCVD, payment.getStatus());
    }

    @Test
    @Tag("integration")
    public void initializeXMLBulkPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {

        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        // Necessary instances for creating a PAIN00100303Document
        PAIN00100303Document document = new PAIN00100303Document();
        CCTInitiation ccInitation = new CCTInitiation();
        GroupHeader groupHeader = new GroupHeader();
        Vector<PaymentInstructionInformation> pmtInfos = new Vector<>();
        // TODO
        //  PmtInf.java is created for temporary usage, until JSEPA supports the added attibute (BtchBookg)
        //  use PaymentInstructionInformation instances and delete PmtInf.java once JSEPA is released
//        PaymentInstructionInformation pii = new PaymentInstructionInformation();
        PmtInf pii = new PmtInf();
        CreditTransferTransactionInformation cdtTrfTxInf1 = new CreditTransferTransactionInformation();
        CreditTransferTransactionInformation cdtTrfTxInf2 = new CreditTransferTransactionInformation();

        // Necessary variables for creating a PAIN00100303Document
        String messageId = "messageId";
        String creationTime = "2019-11-11";
        int numberOfTransactions = 2;
        double controlSum = 200.00;
        String initiatingPartyName = "initiatingPartyName";
        String paymentInformationId = "NOTPROVIDED";
        String paymentMethod = "TRF";
        String requestedExecutionDate = "2019-11-11";
        String debtorName = "Debtor Name";
        String debtorIban = "DE60760300800500123456";
        String debtorBic = "TESTDETT421";
        String chargeBearer = "SLEV";
        boolean batchBooking = true;

        // PAYMENT 1
        double amount1 = 100.00;
        String endToEndID1 = "EndToEndId";
        String creditorName1 = "Hans Handbuch";
        String creditorIBAN1 = "DE98999999990000009999";
        String purpose1 = "purpose string";
        String creditorAgent1 = "AGENT1";

        // PAYMENT 2
        double amount2 = 100.00;
        String endToEndID2 = "EndToEndId";
        String creditorName2 = "Hans Handbuch";
        String creditorIBAN2 = "DE98999999990000009999";
        String purpose2 = "purpose string";
        String creditorAgent2 = "AGENT2";

        // Setting values for each instance
        groupHeader.setMessageId(messageId);
        groupHeader.setCreationTime(creationTime);
        groupHeader.setNoOfTransactions(numberOfTransactions);
        groupHeader.setControlSum(controlSum);
        groupHeader.setInitiatingPartyName(initiatingPartyName);

        cdtTrfTxInf1.setEndToEndID(endToEndID1);
        cdtTrfTxInf1.setAmount(amount1);
        cdtTrfTxInf1.setCreditorName(creditorName1);
        cdtTrfTxInf1.setCreditorIBAN(creditorIBAN1);
        cdtTrfTxInf1.setVwz(purpose1);
        cdtTrfTxInf1.setCreditorAgent(creditorAgent1);

        cdtTrfTxInf2.setEndToEndID(endToEndID2);
        cdtTrfTxInf2.setAmount(amount2);
        cdtTrfTxInf2.setCreditorName(creditorName2);
        cdtTrfTxInf2.setCreditorIBAN(creditorIBAN2);
        cdtTrfTxInf2.setVwz(purpose2);
        cdtTrfTxInf2.setCreditorAgent(creditorAgent2);

        ArrayList<CreditTransferTransactionInformation> list = new ArrayList<>();
        list.add(cdtTrfTxInf1);
        list.add(cdtTrfTxInf2);

        pii.setPmtInfId(paymentInformationId);
        pii.setPaymentMethod(paymentMethod);
        pii.setNoTxns(numberOfTransactions);
        pii.setCtrlSum(controlSum);
        pii.setRequestedExecutionDate(requestedExecutionDate);
        pii.setDebtorName(debtorName);
        pii.setDebtorAccountIBAN(debtorIban);
        pii.setDebitorBic(debtorBic);
        pii.setChargeBearer(chargeBearer);
        pii.setBatchBooking(batchBooking);
        pii.setCreditTransferTransactionInformationVector(list);

        pmtInfos.add(pii);
        ccInitation.setGrpHeader(groupHeader);
        ccInitation.setPmtInfos(pmtInfos);
        document.setCctInitiation(ccInitation);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        // Creating the request instance
        PaymentInitiationPain001Request request = new PaymentInitiationPain001Request(
                PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS, PaymentService.BULK_PAYMENTS, document, psu
        );

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        Assert.assertNotNull(payment);
    }

    @Test
    @Tag("integration")
    public void initiateJSONPayment() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        //payment information
        String creditorIban = "DE98999999990000009999";
        Currency creditorCurrency = Currency.EUR;
        String creditorName = "Max Creditor";
        String debtorIban = "DE60760300800500123456";
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
        paymentBody.setRemittanceInformationUnstructured(reference);

        PSU psu = new PSU("PSU-Successful");
        psu.setIp("192.168.8.78");
        PaymentInitiationJsonRequest request = new PaymentInitiationJsonRequest(PaymentProduct.SEPA_CREDIT_TRANSFERS, paymentBody, psu);
        request.setTppRedirectPreferred(true);

        InitiatedPayment payment = standard.getPis().initiatePayment(request);
        SCAApproach approach = SCAHandler.decision(payment);
        Assert.assertNotNull(payment);
    }
}
